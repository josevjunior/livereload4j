package io.github.josevjunior.livereload4j;

public abstract class PlainApplication {

    public static void launch(String[] args) {

        try {
            StackTraceElement[] cause = Thread.currentThread().getStackTrace();

            boolean foundThisMethod = false;
            String callingClassName = null;
            for (StackTraceElement se : cause) {
                // Skip entries until we get to the entry for this class
                String className = se.getClassName();
                String methodName = se.getMethodName();
                if (foundThisMethod) {
                    callingClassName = className;
                    break;
                } else if (LiveApplication.class.getName().equals(className)
                        && "launch".equals(methodName)) {

                    foundThisMethod = true;
                }
            }

            if (callingClassName == null) {
                throw new RuntimeException("Error: unable to determine LiveApplication class");
            }
            
            
            
            Class appClass = Class.forName(callingClassName);
            if(!LiveApplication.class.isAssignableFrom(appClass)) {
                throw new IllegalArgumentException("Error: " + appClass.getName()
                    + " is not a subclass of jio.github.josevjunior.livereload4j.LiveApplication");
            }
            
            Object app = appClass.newInstance();
            app.getClass().getMethod("start", String[].class).invoke(app, new Object[] { args });
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
