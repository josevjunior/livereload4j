/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.josevjunior.livereload4j.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Jose
 */
public class IOUtils {
    
    public static byte[] readData(InputStream input) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[2048];
        while((read = input.read(buffer)) > 0) {
            baos.write(buffer, 0, read);
        }
        
        input.close();
        
        return baos.toByteArray();
    }
    
     public static void close(Closeable input) throws IOException{
        input.close();
    }
    
}
