
package com.gihub.jvjr.hotreload4j;

import io.github.josevjunior.livereload4j.Project;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class ProjectTest {
    
    @Test
    public void testSourcePathDiscovery() throws Exception {
        
        Assume.assumeNotNull(System.getProperty("user.dir"));
        
        Project project = Project.determineProject();
        Assert.assertEquals(Paths.get(System.getProperty("user.dir"), "src/main/java"), project.getSourcePath().toAbsolutePath());
    }
    
    @Test
    public void testResourcePathDiscovery() throws Exception {
        
        Assume.assumeNotNull(System.getProperty("user.dir"));
        
        Project project = Project.determineProject();
        Assert.assertEquals(Paths.get(System.getProperty("user.dir"), "src/main/resources"), project.getResourcePath().toAbsolutePath());
    }
    
}
