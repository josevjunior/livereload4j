package io.github.josevjunior.livereload4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompilerRunner {

    private final Path sourcePath;
    private final Path javacPath;
    private final String classPath;
    private final Path mainClass;

    public CompilerRunner(Path javacPath, Path sourcePath, String classPath, Path mainClass) {
        this.sourcePath = sourcePath;
        this.javacPath = javacPath;
        this.classPath = classPath;
        this.mainClass = mainClass;
    }

    public void run() throws IOException, InterruptedException {
        
        List<String> commands = new ArrayList<>();
        commands.add(javacPath.toString());
        commands.add("-g");
        commands.add("-sourcepath");
        commands.add(sourcePath.toString());
        if(classPath != null) {
            commands.add("-cp");
            commands.add(classPath);
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
