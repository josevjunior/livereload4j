package io.github.josevjunior.livereload4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompilerRunner {

    private final Path sourcePath;
    private final Path javacPath;
    private String classPath;
    private final Path mainClass;
    private String enconding;

    public CompilerRunner(Path javacPath, Path sourcePath, Path mainClass) {
        this.sourcePath = sourcePath;
        this.javacPath = javacPath;
        this.mainClass = mainClass;
    }

    public void setEnconding(String enconding) {
        this.enconding = enconding;
    }

    public String getEnconding() {
        return enconding;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public Path getSourcePath() {
        return sourcePath;
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
        commands.add(sourcePath.toString());
        if (enconding != null) {
            commands.add("-encoding");
            commands.add(enconding);
        }

        if (classPath != null) {
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
