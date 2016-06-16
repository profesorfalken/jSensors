/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.unix.jna.CSensors;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
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
        //TODO Use PowerShellOperations
        String rawSensorsData = PowerShellOperations.getRawSensorsData();
        
        return normalizeSensorsData(rawSensorsData);
    }
    
    private static String normalizeSensorsData (String rawSensorsData) {
        System.out.println(rawSensorsData);
        
        return "NOTHING";
    }
}