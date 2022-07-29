
package io.github.josevjunior.livereload4j.utils;

import java.security.ProtectionDomain;

public class Env {
    
    public static boolean isFileEnvironment() {
        ProtectionDomain protectionDomain = Env.class.getProtectionDomain();
        return protectionDomain.getCodeSource() == null ? false : protectionDomain.getCodeSource().getLocation().toExternalForm().startsWith("file:");
    }
    
}
