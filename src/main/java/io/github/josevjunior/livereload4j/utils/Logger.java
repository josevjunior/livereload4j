
package io.github.josevjunior.livereload4j.utils;

import java.util.logging.Level;

public class Logger {
    
    private final java.util.logging.Logger logger;
    
    private Logger(Class<?> clazz) {
        logger = java.util.logging.Logger.getLogger(clazz.getName());
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
    
    public void debug(String msg) {
        logger.log(Level.CONFIG, msg);
    }
    
    public void debug(String msg, Throwable throwable) {
        logger.log(Level.CONFIG, msg, throwable);
    }
    
}
