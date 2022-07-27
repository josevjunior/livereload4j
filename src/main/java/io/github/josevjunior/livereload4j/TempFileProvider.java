package io.github.josevjunior.livereload4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempFileProvider {

    public static Path createTempFolder() throws IOException {
        Path tempPath = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "autocmp-");
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            void delete(File file) {
                if (file.isDirectory()) {
                    for (File listFile : file.listFiles()) {
                        delete(listFile);
                    }
                } else {
                    file.delete();
                }
            }

            @Override
            public void run() {
                delete(tempPath.toFile());
            }
        }));

        return tempPath;
    }

}
