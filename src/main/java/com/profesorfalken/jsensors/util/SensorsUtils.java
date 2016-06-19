/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.util;

import com.profesorfalken.jsensors.manager.unix.UnixSensorsManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author javier
 */
public class SensorsUtils {
    public static String generateLibTmpPath(String libName) {
        InputStream in = UnixSensorsManager.class.getResourceAsStream("/" + libName);
        File tempFile;
        try {
            tempFile = File.createTempFile(libName, "");
            byte[] buffer = new byte[1024];
            int read;
            FileOutputStream fos = new FileOutputStream(tempFile);
            while ((read = in.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            in.close();
        } catch (IOException ex) {
            //TODO: handle
            return "";
        }

        return tempFile.getAbsolutePath();
    }
}
