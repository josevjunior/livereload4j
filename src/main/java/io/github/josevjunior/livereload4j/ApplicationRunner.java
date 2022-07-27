package io.github.josevjunior.livereload4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ApplicationRunner {

    private final Project project;
    private final String[] appArgs;
    private final Thread runnerThread;
    private final ClassLoader runnerClassLoader;
    
    private String applicationClassName;
    private Object lastApplicationObject;

    public ApplicationRunner(Project project, String[] appArgs, Thread runnerThread, ClassLoader runnerClassLoader) {
        this.project = project;
        this.appArgs = appArgs;
        this.runnerThread = runnerThread;
        this.runnerClassLoader = runnerClassLoader;
    }

    public String getApplicationClassName() {

        try {
            
            if(applicationClassName != null) {
                return applicationClassName;
            }
            
            StackTraceElement[] stackTrace = runnerThread.getStackTrace();
            StackTraceElement topStackElement = stackTrace[stackTrace.length - 1];
            Class<?> mainClass = Class.forName(topStackElement.getClassName(), false, runnerClassLoader);
            Method method = mainClass.getMethod("main", String[].class);
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new ReflectiveOperationException(mainClass.getName() + ".main(String[]) is not static");
            }

            Class<?> applicationClass = Application.class;
            Class<?> applicationSubClass = null;
            
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                StackTraceElement el = stackTrace[i];
                Class<?> clazz = Class.forName(el.getClassName(), false, runnerClassLoader);
                if (!clazz.getName().equals(applicationClass.getName()) 
                        && applicationClass.isAssignableFrom(clazz)) {
                    clazz.getDeclaredConstructor();
                    applicationSubClass = clazz;
                    break;
                }
            }
            
            if(applicationSubClass == null) {
                throw new ReflectiveOperationException("Could not find a valid Application subclass");
            }
            
            applicationClassName = applicationSubClass.getName();
            return applicationClassName;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public void run() throws Exception{
        
        String applicationClassName = getApplicationClassName();
        if(lastApplicationObject != null) {
            executeStopMethod(lastApplicationObject);
        }
        
        DynamicClassLoader classLoader = new DynamicClassLoader(runnerClassLoader, project.getTempOutputPath().toString());
        Class<?> loadedClass = classLoader.loadClass(applicationClassName);
        lastApplicationObject = loadedClass.newInstance();
        executeStartMethod(lastApplicationObject, appArgs);
        
    }
    
    public void executeStopMethod(Object lastApplicationObject) throws Exception{
        Method stopMethod = lastApplicationObject.getClass().getMethod("stop");
        stopMethod.invoke(lastApplicationObject);
    }
    
    public void executeStartMethod(Object lastApplicationObject, String[] args) throws Exception{
        Method stopMethod = lastApplicationObject.getClass().getMethod("start", String[].class);
        stopMethod.invoke(lastApplicationObject, new Object[] { args });
    }
}
