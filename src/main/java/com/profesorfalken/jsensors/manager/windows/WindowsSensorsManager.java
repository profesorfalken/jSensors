/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jsensors.manager.SensorsManager;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public class WindowsSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSensorsManager.class);

    private static final String LINE_BREAK = "\n";
    
    private final StringBuilder sensorsData = new StringBuilder();
    private final StringBuilder sensorsDebugData = new StringBuilder();

    public String getSensorsData() {
        PowerShell powershell = null;
        try {
            powershell = PowerShell.openSession();
            String returnedvalue = powershell.executeCommand("([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] \"Administrator\")").getCommandOutput();
            System.out.println("Executed Windows : " + returnedvalue);
        } catch (PowerShellNotAvailableException ex) {
            //TODO: Handle error
        } finally  {
            if (powershell != null) {
                powershell.close();
            }
        }        
        
                
        return null;
    }
    
    private static String getPowerShellScript() {
        StringBuilder script = new StringBuilder();
        
        script.append(PowerShellScriptHelper.dllImport());     
        script.append(PowerShellScriptHelper.newComputerInstance());        
        script.append(PowerShellScriptHelper.sensorsQueryLoop());
        
        return script.toString();
    }
}