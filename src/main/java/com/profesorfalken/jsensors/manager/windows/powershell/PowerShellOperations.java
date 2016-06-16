/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows.powershell;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;

/**
 *
 * @author Javier
 */
public class PowerShellOperations {
    public static boolean isAdministrator() {
        String command = "([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] \"Administrator\")";
        
        return "true".equalsIgnoreCase(PowerShell.executeSingleCommand(command).getCommandOutput());
    }
    
    public static String getRawSensorsData() {
        PowerShell powershell = null;
        String rawData = null;
        try {
            powershell = PowerShell.openSession();
            powershell.executeCommand(PowerShellScriptHelper.generateScript());           
            rawData = powershell.executeCommand(PowerShellScriptHelper.generateScript()).getCommandOutput();
        } catch (PowerShellNotAvailableException ex) {
            //TODO: Handle error
        } finally  {
            if (powershell != null) {
                powershell.close();
            }
        }   
        return rawData;
    }
}
