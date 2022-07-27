package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.Logger;
import java.nio.file.Path;

public interface Application {
    
    void start(String[] args) throws Exception;

    default void stop() throws Exception {
        // this method can be overrided in case of resource dispose
    }

    public static void launch(String[] args) {

        try {
            
            Logger LOGGER = Logger.getLogger(Application.class);
            
            Project project = Project.determineProject();
            
            Path tempFolderPath = TempFileProvider.createTempFolder();
            project.setTempOutputPath(tempFolderPath);
            
            ApplicationRunner appRunner = new ApplicationRunner(project, args, Thread.currentThread(), Thread.currentThread().getContextClassLoader());            
            HotReloadServiceV1 hotReloadService = new HotReloadServiceV1(project, appRunner);            
            hotReloadService.start();
            
//
//            
//            Class<?> mainClass = mainClassLoader.loadClass(topStackElement.getClassName());
//            try {
//                Method mainMethod = mainClass.getMethod("main", String[].class);
//                if(!Modifier.isStatic(mainMethod.getModifiers())) {
//                    throw new NoSuchMethodError();
//                }
//            }catch (NoSuchMethodException e) {
//                LOGGER.log(Level.CONFIG, "launch method is not static nor running in main thread", e);
//                throw e;
//            }
//            
//            Path projectHome = Paths.get(System.getProperty("user.dir"));
//            
//            HotReloadServiceV1 reloadService = new HotReloadServiceV1(projectHome.toFile().getAbsolutePath(), mainClassLoader, mainClass, args);
//            reloadService.start();
//            String mainClassPath = reloadService.getTempFolderPath().toFile().getAbsolutePath() + PathUtils.FILE_SEPARATOR + mainClass.getName().replace('.', PathUtils.FILE_SEPARATOR.charAt(0)) + ".java";
//            
//            CompilerRunner compilerRunner = new CompilerRunner(
//                    javacPath.toFile().getAbsolutePath(),
//                    reloadService.getTempFolderPath().toFile().getAbsolutePath(),
//                    System.getProperty("java.class.path"), 
//                    mainClassPath
//            );
//            compilerRunner.run();
//
//            
//            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(mainClassLoader, reloadService.getTempFolderPath().toFile().getAbsolutePath());
//            StackTraceElement[] stackTrace = mainThread.getStackTrace();
//            
//            Class<?> applicationClass = dynamicClassLoader.loadClass(Application.class.getName());
//            Object application = null;
//            for (int i = stackTrace.length - 1; i >= 0; i--) {
//                StackTraceElement el = stackTrace[i];
//                    Class<?> clazz = dynamicClassLoader.loadClass(el.getClassName());
//                    if (applicationClass.isAssignableFrom(clazz)) {
//                        application = clazz.newInstance();
//                        break;
//                    }
//            }
//            
//            Method startMethod = application.getClass().getMethod("start", String[].class);
//            startMethod.invoke(application, new Object[] { args });
//            
            //System.out.println("Application: " + application);
            /**
             * StackTraceElement[] stackTrace = mainThread.getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement el = stackTrace[i];
            Class<?> clazz = dynamicClassLoader.loadClass(el.getClassName());
            if (applicationClass.isAssignableFrom(clazz)) {
            application = clazz.newInstance();
            break;
            }
            }
             */
            //String classPath = System.getProperty("java.class.path");
            /*ClasspathLoader classpathLoader = new ClasspathLoader(classPath, args);
            classpathLoader.load();*/


           
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
