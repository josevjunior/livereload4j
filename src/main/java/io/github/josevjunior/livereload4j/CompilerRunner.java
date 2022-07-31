package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompilerRunner {

    private final Project project;
    private final Path javacPath;
    private String classPath;
    private final Path mainClass;
    private String encoding;

    public CompilerRunner(Project project, Path javacPath, Path mainClass) {
        this.project = project;
        this.javacPath = javacPath;
        this.mainClass = mainClass;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public Project getProject() {
        return project;
    }

    public Path getJavacPath() {
        return javacPath;
    }

    public Path getMainClass() {
        return mainClass;
    }

    public void run() throws IOException, InterruptedException {

        List<String> commands = new ArrayList<>();
        commands.add(javacPath.toString());
        
        commands.add("-g");
        commands.add("-sourcepath");
        commands.add(project.getSourcePath().toAbsolutePath().toString());
        
        commands.add("-d");
        commands.add(project.getTempOutputPath().toAbsolutePath().toString());
        
        if (encoding != null) {
            commands.add("-encoding");
            commands.add(encoding);
        }
        
        if (classPath != null) {
            
            String resourcePath = project.getRootPath().toAbsolutePath().toString();
            
            // Some IDE's include the compiled source folder path as part of
            // classpath. This is bad cause depends on IDE compilation time.
            // We'll ignore this kind of path to avoid this situation
            String realClasspath = Stream.of(classPath.split(File.pathSeparator))
                                    .filter(strPath -> {
                                        Path path = Paths.get(strPath);
                                        return !(path.startsWith(resourcePath) && Files.isDirectory(path));
                                    })
                                    .collect(Collectors.joining(File.pathSeparator));
                                    
                                     
            commands.add("-cp");
            commands.add(realClasspath);            
        }
        commands.add(mainClass.toString());

        ProcessBuilder pb = new ProcessBuilder(commands);
        Process process = pb.start();
        int result = process.waitFor();

        if (result != 0) {
            Scanner scanner = new Scanner(process.getErrorStream());
            StringBuilder msg = new StringBuilder();
            try {
                while (scanner.hasNextLine()) {
                    msg.append(scanner.nextLine());
                }
            } finally {
                scanner.close();
            }

            throw new IOException(msg.toString());

        }
    }

}
