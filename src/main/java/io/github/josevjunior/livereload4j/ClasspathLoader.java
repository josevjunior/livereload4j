package io.github.josevjunior.livereload4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClasspathLoader {

    private final String path;
    private final String[] args;

    public ClasspathLoader(String classPath, String[] args) {
        this.path = classPath;
        this.args = args;
    }

    public void reload() {
        System.gc();
        load();
    }
    
    public void load() {

        Thread mainThread = Thread.currentThread();
        ClassLoader mainClassLoader = mainThread.getContextClassLoader();
        try {
            
            Object application = null;

            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(mainClassLoader, path);
            mainThread.setContextClassLoader(dynamicClassLoader);

            Class<?> applicationClass = dynamicClassLoader.load(LiveApplication.class.getName());

            StackTraceElement[] stackTrace = mainThread.getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                StackTraceElement el = stackTrace[i];
                Class<?> clazz = dynamicClassLoader.loadClass(el.getClassName());
                if (applicationClass.isAssignableFrom(clazz)) {
                    application = clazz.newInstance();
                    break;
                }
            }

            if (application == null) {
                throw new IllegalAccessException("Could not start application");
            }

            Method method = application.getClass().getMethod("start", String[].class);
            method.invoke(application, new Object[]{args});
            
            dynamicClassLoader = null;
            System.gc();

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException as) {
            as.printStackTrace();
        }
    }

}
