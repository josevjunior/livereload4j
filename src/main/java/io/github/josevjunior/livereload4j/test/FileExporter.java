
package io.github.josevjunior.livereload4j.test;

import io.github.josevjunior.livereload4j.Application;
import io.github.josevjunior.livereload4j.CompilerRunner;
import io.github.josevjunior.livereload4j.DynamicClassLoader;
import io.github.josevjunior.livereload4j.utils.PathUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class FileExporter {
    
//    public static void main(String[] args) throws Exception {
//  
//        
////        ProjectPattern pattern = null;
////        for (ProjectPattern value : ProjectPattern.values()) {
////            if(new File(value.getSourcePath()).exists()){
////                pattern = value;
////                break;
////            }
////        }
////        
////        System.getProperties().forEach((k,v) -> System.out.println(k + " = " + v));
////        
////        if(pattern == null) {
////            throw new RuntimeException("Cannot inffer the project path pattern");
////        }
////        
////        Path tempDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "autocmp-");
////        System.out.println(tempDir.toFile().getAbsolutePath());
////        
////        Path path = new File(pattern.getSourcePath()).toPath();
////        copy(path, tempDir);
//        
//        System.getProperties().forEach((k, v) -> System.out.println(k + " = " + v));
//        
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        
//        InputStream in = new ByteArrayInputStream(new byte[1024]);
//        OutputStream out = new ByteArrayOutputStream();
//        
//        Path javacPath = PathUtils.getJavacPath(System.getProperty("java.home"));
//        
//        CompilerRunner runner = new CompilerRunner(javacPath.toString(), "C:\\Users\\Jose\\AppData\\Local\\Temp\\autocmp-9103732927163214533", null, "C:\\Users\\Jose\\AppData\\Local\\Temp\\autocmp-9103732927163214533\\br\\com\\jvjr\\hotreload4j\\test\\MainV2.java");
//        long begin = System.currentTimeMillis();
//        runner.run();
//        long end = System.currentTimeMillis();
//        
//        
//        DynamicClassLoader classLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader(), "C:\\Users\\Jose\\AppData\\Local\\Temp\\autocmp-9103732927163214533");
//        Class<?> clazz = classLoader.load(Application.class.getName());
//        System.out.println(clazz.newInstance());
//        //compiler.run(null, null, null, "-sourcepath", "C:\\Users\\Jose\\AppData\\Local\\Temp\\autocmp-9103732927163214533", "C:\\Users\\Jose\\AppData\\Local\\Temp\\autocmp-9103732927163214533\\br\\com\\jvjr\\hotreload4j\\test\\MainV2.java");
//        
//        
//    }
//    
//    public static void copy(Path path, Path out) throws IOException {
//        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                
//                Path outFile = out.resolve(file.subpath(3, file.getNameCount()));
//                outFile.toFile().getParentFile().mkdirs();
//                Files.copy(file, outFile, StandardCopyOption.REPLACE_EXISTING);
//                
//                return FileVisitResult.CONTINUE;
//            }            
//        });
//    }
    
}
