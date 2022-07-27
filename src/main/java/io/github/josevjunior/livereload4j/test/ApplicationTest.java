
package io.github.josevjunior.livereload4j.test;

import io.github.josevjunior.livereload4j.DynamicClassLoader;
import java.lang.reflect.Method;
import java.util.Scanner;

public abstract class ApplicationTest {
    
    public abstract void start(String[] args) throws Exception;
    
    public static void start0(String[] args) {

        try {

            StackTraceElement element = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];
            System.out.println(element.getMethodName());
            /*for (;;) {
                Object application = null;
                Thread mainThread = Thread.currentThread();
                ClassLoader mainClassLoader = mainThread.getContextClassLoader();
                DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(mainClassLoader, "target/classes");
                mainThread.setContextClassLoader(dynamicClassLoader);

                
                Class<?> applicationClass = dynamicClassLoader.load(ApplicationTest.class.getName());
                
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
                
                method.invoke(application, new Object[] { args });                
                
                mainThread.setContextClassLoader(mainClassLoader);
                dynamicClassLoader = null;
                System.gc();
                
                new Scanner(System.in).nextInt();
            }*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
}
