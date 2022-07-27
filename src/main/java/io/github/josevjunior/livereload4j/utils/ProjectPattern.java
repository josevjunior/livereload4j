
package io.github.josevjunior.livereload4j.utils;

@Deprecated
public enum ProjectPattern {

    // The logger the path, the higher the position
    MAVEN("src/main/java", 3),
    PLAIN("src", 1);

    private final String sourcePath;
    private final int pathDeepness;
        
    private ProjectPattern(String sourcePath, int pathDeepness) {
        this.sourcePath = sourcePath;
        this.pathDeepness = pathDeepness;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public int getPathDeepness() {
        return pathDeepness;
    }
    
}
