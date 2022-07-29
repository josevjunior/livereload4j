
package io.github.josevjunior.livereload4j.utils;

import io.github.josevjunior.livereload4j.ConfProvider;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class Logger {
    
    private final java.util.logging.Logger logger;
    private final String type;
    private final boolean enabled;
    
    private static final Pattern pattern;
    
    static {
        String regex = ConfProvider.getEnabledLogRegex();
        if(regex != null) {
            pattern = Pattern.compile(regex);
        } else {
            pattern = null;
        }
    }
    
    private Logger(Class<?> clazz, String type) {
        this.type = type;
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new SimpleFormatter());        
        
        logger = java.util.logging.Logger.getLogger(clazz.getName());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);
                
        enabled = pattern != null && pattern.matcher(type).find();
    }
    
    public static Logger getLogger(Class<?> clazz, String type) {
        return new Logger(clazz, type);
    }
    
    public void debug(String msg) {
        
        if(!enabled) {
            return;
        }
        
        logger.log(Level.FINE, "[" + type + "] " + msg);
    }
    
    public void debug(String msg, Throwable throwable) {
        
        if(!enabled) {
            return;
        }
        
        logger.log(Level.FINE, "[" + type + "]" + msg, throwable);
    }
    
}
