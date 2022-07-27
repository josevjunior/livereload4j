package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.PathUtils;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Project {

    public static Project determineProject() {
        
        String projectDir = System.getProperty("user.dir");
        if(projectDir == null) {
            projectDir = PathUtils.FILE_SEPARATOR;
        }
        
        Path rootPath = Paths.get(projectDir);

        Object[][] options = {
            {"src/main/java", true, "src/main/resources"},
            {"src", false, null}
        };

        for (Object[] option : options) {
            Path path = Paths.get((String) option[0]);
            if (exists(path) && isDirectory(path)) {

                boolean useResource = (boolean) option[1];

                Project project = new Project();
                project.setRootPath(rootPath);
                project.setSourcePath(path);
                if (useResource) {
                    Path resourcePath = Paths.get((String) option[2]);
                    if (exists(resourcePath) && isDirectory(resourcePath)) {
                        project.setUseResourcePath(true);
                        project.setResourcePath(resourcePath);
                    }
                }

                return project;

            }
        }

        throw new IllegalStateException("Could not determine the project source path");

    }

    private Path rootPath;
    private Path sourcePath;
    private boolean useResourcePath;
    private Path resourcePath;

    private Path tempOutputPath;

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public boolean isUseResourcePath() {
        return useResourcePath;
    }

    public void setUseResourcePath(boolean useResourcePath) {
        this.useResourcePath = useResourcePath;
    }

    public Path getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(Path resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Path getTempOutputPath() {
        return tempOutputPath;
    }

    public void setTempOutputPath(Path tempOutputPath) {
        this.tempOutputPath = tempOutputPath;
    }

    @Override
    public String toString() {
        return "Project{" + "sourcePath=" + sourcePath + ", useResourcePath=" + useResourcePath + ", resourcePath=" + resourcePath + ", tempOutputPath=" + tempOutputPath + '}';
    }

}
