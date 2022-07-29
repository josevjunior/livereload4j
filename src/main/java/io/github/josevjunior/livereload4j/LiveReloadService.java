package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.Logger;
import io.github.josevjunior.livereload4j.utils.PathUtils;
import io.github.josevjunior.livereload4j.utils.StringUtils;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class LiveReloadService implements Consumer<FileChangeEvent> {

    private final static Logger LOGGER = Logger.getLogger(LiveReloadService.class, LoggerType.FOLDERWATCHER);
    
    private ObserverKey observerKey;
    private final CompilerRunner compilerRunner;
    private final Project project;
    private final TickWaiter tickWaiter;
    private final Pattern ignorePattern;
    private final ApplicationRunner runner;

    public LiveReloadService(Project project, ApplicationRunner runner) {

        try {            
         
            this.project = project;
            this.runner = runner;
            
            Path javacPath = PathUtils.getJavacPath(System.getProperty("java.home"));
            Path mainClassPath = getMainClassPath();
            
            compilerRunner = new CompilerRunner(
                    javacPath, 
                    project.getTempOutputPath(),
                    mainClassPath
            );
            compilerRunner.setClassPath(StringUtils.getEmptyAsNull(System.getProperty("java.class.path")));
            compilerRunner.setEnconding(StringUtils.getEmptyAsNull(ConfProvider.getFileEncode()));
            
            tickWaiter = new TickWaiter(() -> {
                try {
                    compileClassesAndRun();
                } catch (Exception ex) {
                    LOGGER.debug(ex.getLocalizedMessage(), ex);
                }
            });
            
            ignorePattern = Pattern.compile(ConfProvider.getFileIgnoreRegex());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
        

    }

    public void start() throws Exception {
        registerFileWatcher();
        moveFilesToTempFolder();
        
        compileClassesAndRun();
    }
    
    private void compileClassesAndRun() throws Exception {
        compilerRunner.run();
        runner.run();
    }
    
    private Path getMainClassPath() {
        return project.getTempOutputPath().resolve(runner.getApplicationClassName().replace('.', PathUtils.FILE_SEPARATOR.charAt(0)) + ".java");
    } 

    public void stop() {
        observerKey.unregister();
    }
    
    private boolean shouldIgnore(FileChangeEvent changeEvent) {
        return ignorePattern.matcher(changeEvent.getPathChanged().toString()).find();
    }

    @Override
    public void accept(FileChangeEvent fileChange) {

        try {
            
            LOGGER.debug(String.format("Event fired: %s", fileChange.getEventKind()));
            LOGGER.debug(String.format("File changed: %s", fileChange.getPathChanged()));
            
            if(shouldIgnore(fileChange)) {
                LOGGER.debug("ignored");
                return;
            }
            
            Path fileChangedPath = fileChange.getPathChanged().toAbsolutePath();
            
            Path targetSubpath = fileChangedPath.subpath(project.getRootPath().toAbsolutePath().getNameCount(), fileChangedPath.getNameCount());
            Path targetFilePath = null;
            if(project.isUseResourcePath() && targetSubpath.startsWith(project.getResourcePath())){
                targetFilePath = targetSubpath.subpath(project.getResourcePath().getNameCount(), targetSubpath.getNameCount());                
            } else {                
                targetFilePath = targetSubpath.subpath(project.getSourcePath().getNameCount(), targetSubpath.getNameCount());                
            }
            targetFilePath = project.getTempOutputPath().resolve(targetFilePath);
            
            if (fileChange.getEventKind() == StandardWatchEventKinds.ENTRY_CREATE) {                                                
                
                copyFile(fileChangedPath, targetFilePath);
                
            } else if (fileChange.getEventKind() == StandardWatchEventKinds.ENTRY_MODIFY) {                
                
                if(!Files.isDirectory(targetFilePath) || (!targetFilePath.toFile().exists() && fileChangedPath.toFile().exists())) {
                    copyFile(fileChangedPath, targetFilePath); 
                }
                
            } else if (fileChange.getEventKind() == StandardWatchEventKinds.ENTRY_DELETE) {
                
                deleteFile(targetFilePath);
                
            }
            
            tickWaiter.notifyOrWait();
            
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void registerFileWatcher() throws IOException {
        observerKey = FolderChangeScanner.registerObserver(project, this);
    }

    private void moveFilesToTempFolder() throws IOException {
        
        class SimpleFileVisitorImpl extends SimpleFileVisitor<Path> {

            private final Path tempOutputPath;
            private final Path sourcePath;
            
            public SimpleFileVisitorImpl(Path tempOutputPath, Path sourcePath) {
                this.tempOutputPath = tempOutputPath;
                this.sourcePath = sourcePath;                
            }
            
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                
                Path outFile = tempOutputPath.resolve(file.subpath(sourcePath.getNameCount(), file.getNameCount()));
                outFile.toFile().getParentFile().mkdirs();
                Files.copy(file, outFile, StandardCopyOption.REPLACE_EXISTING);
                
                return FileVisitResult.CONTINUE;
            }
        }
        
        Files.walkFileTree(project.getSourcePath(), new SimpleFileVisitorImpl(
                project.getTempOutputPath().toAbsolutePath(), 
                project.getSourcePath()
        ));
        
        if(project.isUseResourcePath()) {
            
            Files.walkFileTree(project.getResourcePath(), new SimpleFileVisitorImpl(
                project.getTempOutputPath().toAbsolutePath(), 
                project.getResourcePath()
            ));
            
        }
    }

    private void copyFile(Path source, Path target) throws IOException {
        if(Files.isDirectory(source)) {
            try {
                Files.createDirectories(target);
            }catch (IOException e){
                LOGGER.debug(e.getMessage(), e);
            }            
        } else {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }        
    }
    
    private void deleteFile(Path path) throws IOException {
        try {
            Files.deleteIfExists(path);
        }catch (DirectoryNotEmptyException e) {
            LOGGER.debug(e.getMessage(), e);
        }
        
    }

}
