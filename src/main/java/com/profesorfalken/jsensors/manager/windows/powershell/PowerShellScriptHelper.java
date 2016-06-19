/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows.powershell;

import com.profesorfalken.jsensors.util.SensorsUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author Javier
 */
class PowerShellScriptHelper {

    private static final String LINE_BREAK = "\r\n";

    private static String dllImport() {
        return "[System.Reflection.Assembly]::LoadFile(\"" + 
                SensorsUtils.generateLibTmpPath("OpenHardwareMonitorLib.dll") + 
                "\")" + 
                " | Out-Null" + LINE_BREAK;
    }
    
    private static String newComputerInstance() {
        StringBuilder code = new StringBuilder();

        code.append("$PC = New-Object OpenHardwareMonitor.Hardware.Computer").append(LINE_BREAK);

        code.append("$PC.MainboardEnabled = $true").append(LINE_BREAK);
        code.append("$PC.CPUEnabled = $true").append(LINE_BREAK);
        code.append("$PC.RAMEnabled = $true").append(LINE_BREAK);
        code.append("$PC.GPUEnabled = $true").append(LINE_BREAK);
        code.append("$PC.FanControllerEnabled = $true").append(LINE_BREAK);
        code.append("$PC.HDDEnabled = $true").append(LINE_BREAK);

        return code.toString();
    }

    private static String sensorsQueryLoop() {
        StringBuilder code = new StringBuilder();

        code.append("$PC.Open()").append(LINE_BREAK);

        code.append("ForEach ($hw in $PC.Hardware)").append(LINE_BREAK);
        code.append("{").append(LINE_BREAK);
        code.append("$hw").append(LINE_BREAK);
        code.append("$hw.Update()").append(LINE_BREAK);
        code.append("ForEach ($subhw in $hw.SubHardware)").append(LINE_BREAK);
        code.append("{").append(LINE_BREAK);
        code.append("$subhw.Update()").append(LINE_BREAK);
        code.append("}").append(LINE_BREAK);
        code.append("ForEach ($sensor in $hw.Sensors)").append(LINE_BREAK);
        code.append("{").append(LINE_BREAK);
        code.append("$sensor").append(LINE_BREAK);
        code.append("Write-Host \"\"").append(LINE_BREAK);
        code.append("}").append(LINE_BREAK);
        code.append("}");

        return code.toString();
    }

    static String generateScript() {
        File tmpFile = null;
        FileWriter writer = null;

        try {
            tmpFile = File.createTempFile("jsensors_" + new Date().getTime(), ".ps1");
            writer = new FileWriter(tmpFile);
            writer.write(getPowerShellScript());
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            //TODO: handle
            ex.printStackTrace();
            return "Error";
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                //TODO: handle
                ioe.printStackTrace();
            }
        }
        return tmpFile.getAbsolutePath();
    }

    private static String getPowerShellScript() {
        StringBuilder script = new StringBuilder();

        script.append(dllImport());
        script.append(newComputerInstance());
        script.append(sensorsQueryLoop());

        return script.toString();
    }
}
