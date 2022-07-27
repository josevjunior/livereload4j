package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.Logger;
import java.nio.file.Path;

/**
 * The interface indicates a program who will be made livereloadable. 
 * <br>Must</br> the same class invoker of {@link LiveApplication#start(java.lang.String[]) start} 
 * method which the {@code public static void main(java.lang.String[])} will trigger the 
 * {@link LiveApplication#launch(java.lang.String[]) launch method}
 */
public interface LiveApplication {
    
    /**
     * In a LiveApplication environment, the program entrypoint needs a facade to
     * abstract the initialization. So, the main method will start the reloadable container
     * that will execute this method every time a change is detected in the enviroment
     */
    void start(String[] args) throws Exception;

    /**
     * This method can be overrided to dipose/close/stop any resource that 
     * cannot be cleaned during a reference cleanup
     */
    default void stop() throws Exception {
        // this method can be overrided in case of resource dispose
    }

    /**
     * Start the live reload container which will handle the instantiation of the {@link LiveApplication LiveApplication}
     * implementation and execution of {@link LiveApplication#start(java.lang.String[]) start} and {@link LiveApplication#stop() stop}
     * methods
     */
    public static void launch(String[] args) {

        try {
            
            Logger LOGGER = Logger.getLogger(LiveApplication.class);
            
            Project project = Project.determineProject();
            
            Path tempFolderPath = TempFileProvider.createTempFolder();
            project.setTempOutputPath(tempFolderPath);
            
            ApplicationRunner appRunner = new ApplicationRunner(project, args, Thread.currentThread(), Thread.currentThread().getContextClassLoader());            
            LiveReloadService liveReloadService = new LiveReloadService(project, appRunner);            
            liveReloadService.start();

           
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
