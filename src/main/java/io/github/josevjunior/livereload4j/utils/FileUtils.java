
package io.github.josevjunior.livereload4j.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    
    public static byte[] readFileToBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        }catch (IOException e) {
            throw  new RuntimeException(e);
        }        
    }
    
}
