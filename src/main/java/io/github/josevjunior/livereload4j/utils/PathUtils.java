
package io.github.josevjunior.livereload4j.utils;

import java.io.File;
import java.nio.file.Path;

public class PathUtils {
    
    public static final String FILE_SEPARATOR = File.separator;
    
     
    public static Path getJavacPath(String javaHome){
        if(javaHome == null) {
            throw new NullPointerException("java.path");
        }
        
        File file = new File(javaHome);
        if(!file.exists()) {
            throw new IllegalStateException("java.path argument is a invalid path or doest not exist in file system: " + javaHome);
        }
        
        File jdkFolder = null;        
        if(file.getAbsolutePath().endsWith("jre")) {
            jdkFolder = file.getParentFile();
        } else {
            jdkFolder = file;
        }
        
        File javacFile = new File(jdkFolder.getAbsolutePath() + FILE_SEPARATOR + "/bin/javac");
        if(javacFile.exists()) {
            throw new IllegalStateException("Could not infer javac path from java.path: " + javaHome);
        }
        
        return javacFile.toPath();
        
    }
    
}
