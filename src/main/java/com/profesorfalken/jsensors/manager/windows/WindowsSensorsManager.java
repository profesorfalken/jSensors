/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public class WindowsSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSensorsManager.class);

    private static final String LINE_BREAK = "\r\n";
    
    private final StringBuilder sensorsData = new StringBuilder();
    private final StringBuilder sensorsDebugData = new StringBuilder();

    public String getSensorsData() {
        String rawSensorsData = PowerShellOperations.getRawSensorsData();
        
        if (debugMode) {
            LOGGER.info(rawSensorsData);
        }
        
        return normalizeSensorsData(rawSensorsData);
    }
    
    private static String normalizeSensorsData (String rawSensorsData) {
        StringBuilder normalizedSensorsData = new StringBuilder();
        String[] dataLines = rawSensorsData.split("\\r?\\n");
        
        boolean readingHardLabel = false;
        boolean readingSensor = false;
        for (final String dataLine : dataLines) {
            if (readingHardLabel == false && "HardwareType".equals(getKey(dataLine))) { 
                String hardwareType = getValue(dataLine);
                if ("CPU".equals(hardwareType)) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("CPU").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                } else if (hardwareType.toUpperCase().startsWith("GPU")) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("GPU").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                } else if ("HDD".equals(hardwareType)) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("DISK").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                }
            }
            
            if (readingHardLabel) {
                if ("Name".equals(getKey(dataLine))) {
                    normalizedSensorsData.append("Label: ").append(getValue(dataLine)).append(LINE_BREAK);
                    readingHardLabel = false;
                }
            } else {            
                if ("SensorType".equals(getKey(dataLine))) { 
                    String sensorType = getValue(dataLine);
                    if ("Temperature".equals(sensorType) || "Fan".equals(sensorType)) {
                        readingSensor = true;
                        continue;
                    }            
                }     
                if (readingSensor) {
                    if ("Name".equals(getKey(dataLine))) {
                        normalizedSensorsData.append(getValue(dataLine)).append(": ");
                    } else if ("Value".equals(getKey(dataLine))) {
                        normalizedSensorsData.append(getValue(dataLine)).append(LINE_BREAK);
                        readingSensor = false;
                    }
                }
            }
        }
        
        return normalizedSensorsData.toString();
    }
    
    private static String getKey(String line) {
        return getData(line, 0);
    }
    
    private static String getValue(String line) {        
        return getData(line, 1);
    }
    
    private static String getData(String line, final int index) {
        if (line.indexOf(":") > 0) {
            return line.split(":", 2)[index].trim();
        }
        
        return "";
    }
}