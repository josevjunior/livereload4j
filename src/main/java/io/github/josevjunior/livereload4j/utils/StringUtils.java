
package io.github.josevjunior.livereload4j.utils;

public class StringUtils {
    
    public static String getNullAsEmpty(String str) {
        return str == null ? "" : str;
    }
    
    public static String getEmptyAsNull(String str) {
        return "".equals(getNullAsEmpty(str)) ? null : str;
    }
    
}
