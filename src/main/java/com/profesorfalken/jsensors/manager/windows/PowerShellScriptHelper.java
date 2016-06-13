/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Javier
 */
class PowerShellScriptHelper {

    static String dllImport() {
        return "[System.Reflection.Assembly]::LoadFile(\"" + dllPath() + "\")"
                + " | Out-Null";
    }

    private static String dllPath() {
        String dllName = "OpenHardwareMonitorLib.dll";
        InputStream in = PowerShellScriptHelper.class.getResourceAsStream(dllName);
        File tempFile;
        try {
            tempFile = File.createTempFile(dllName, "");
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

    static String newComputerInstance() {
        StringBuilder code = new StringBuilder();

        code.append("$PC = New-Object OpenHardwareMonitor.Hardware.Computer");

        code.append("$PC.MainboardEnabled = $true");
        code.append("$PC.CPUEnabled = $true");
        code.append("$PC.RAMEnabled = $true");
        code.append("$PC.GPUEnabled = $true");
        code.append("$PC.FanControllerEnabled = $true");
        code.append("$PC.HDDEnabled = $true");

        return code.toString();
    }

    static String sensorsQueryLoop() {
        StringBuilder code = new StringBuilder();

        code.append("$PC.Open()");

        code.append("ForEach ($hw in $PC.Hardware)");
        code.append("{");
        code.append("$hw");
        code.append("$hw.Update()");
        code.append("ForEach ($subhw in $hw.SubHardware)");
        code.append("{");
        code.append("$subhw.Update()");
        code.append("}");
        code.append("ForEach ($sensor in $hw.Sensors)");
        code.append("{");
        code.append("$sensor");
        code.append("Write-Host \"\"");
        code.append("}");
        code.append("}");

        return code.toString();
    }
}
