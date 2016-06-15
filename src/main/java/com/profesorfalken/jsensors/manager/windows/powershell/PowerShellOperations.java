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
        try {
            powershell = PowerShell.openSession();
            //Checks if running as administrator
            String returnedvalue = powershell.executeCommand("([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] \"Administrator\")").getCommandOutput();
            System.out.println("Executed Windows : " + returnedvalue);
            System.out.println("Executing script:" + PowerShellScriptHelper.generateScript());
            returnedvalue = powershell.executeCommand(PowerShellScriptHelper.generateScript()).getCommandOutput();
            System.out.println("After executing script:" + returnedvalue);
            returnedvalue = powershell.executeCommand(PowerShellScriptHelper.generateScript()).getCommandOutput();
            System.out.println("After executing script:" + returnedvalue);
        } catch (PowerShellNotAvailableException ex) {
            //TODO: Handle error
        } finally  {
            if (powershell != null) {
                powershell.close();
            }
        }   
        return null;
    }
}
