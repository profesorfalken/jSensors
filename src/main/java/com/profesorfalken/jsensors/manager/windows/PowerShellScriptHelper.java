/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows;

/**
 *
 * @author Javier
 */
class PowerShellScriptHelper {
    static String dllImport() {
        return "[System.Reflection.Assembly]::LoadFile(\"c:\\OpenHardwareMonitorLib.dll\")"
                + " | Out-Null";
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
