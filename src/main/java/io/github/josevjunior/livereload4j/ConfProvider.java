
package io.github.josevjunior.livereload4j;

public class ConfProvider {
    
    /**
     * 
     * @return 
     */
    public static String getFileIgnoreRegex() {
        return System.getProperty("lr4j.file.ignore.regex", "~$");
    }
    
    public static String getEnabledLogRegex() {
        return System.getProperty("lr4j.enabled.log.regex");
    }
    
}
