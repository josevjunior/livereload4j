package io.github.josevjunior.livereload4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TempFileProvider {

    private static final String TEMP_FILE_PREFIX = ".lr4j-";
    private static final String TEMP_FOLDER_PREFIX = "lr4j-";

    public static Path createTempFolder() throws IOException {

        Path tempFolder = Paths.get(System.getProperty("java.io.tmpdir"));
        Path lockFile = returnAvailableLockedFile(tempFolder);        
        
        Path projectTempFolder = tempFolder.resolve(lockFile.getFileName().toString().substring(1)); // Remove the .
        if(Files.notExists(projectTempFolder)) {
            Files.createDirectory(projectTempFolder);
        } else {
            cleanDirectoryContent(projectTempFolder);
        }
        
        return  projectTempFolder;
    }

    private static void cleanDirectoryContent(final Path startDir) {
        delete(startDir.toFile());
    }
    
    private static void delete(File file) {
        if(file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                delete(listFile);
            }
        } else {
            file.delete();
        }
    }
    
    private static Path returnAvailableLockedFile(Path dirToFind) throws IOException {

        Path lockedFile = null;
        
        List<Path> lockFiles = Files.list(dirToFind)
                .filter(path -> path.toString().contains(TEMP_FILE_PREFIX))
                .collect(Collectors.toList());

        int lastLockNumber = 0;
        boolean isLocked = false;

        for (Path lockFile : lockFiles) {

            if (!isLocked) {
                RandomAccessFile accessFile = new RandomAccessFile(lockFile.toFile(), "rw");
                FileChannel channel = accessFile.getChannel();
                FileLock tryLock = channel.tryLock();
                if (tryLock != null) {
                    isLocked = true;
                    lockedFile = lockFile;
                } else {
                    accessFile.close();
                }
            }

            String fileName = lockFile.getFileName().toString();
            int lockNumber = Integer.parseInt(fileName.substring(fileName.lastIndexOf("-") + 1));
            lastLockNumber = lockNumber > lastLockNumber ? lockNumber : lastLockNumber;
        }
        
        if(!isLocked) {
            lockedFile = dirToFind.resolve(TEMP_FILE_PREFIX + (lastLockNumber + 1));
            RandomAccessFile accessFile = new RandomAccessFile(lockedFile.toFile(), "rw");
            accessFile.getChannel().lock();
        }

        return lockedFile;
    }

}
