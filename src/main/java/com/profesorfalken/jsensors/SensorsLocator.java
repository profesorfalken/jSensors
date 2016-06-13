/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.stub.StubSensorsManager;
import com.profesorfalken.jsensors.manager.unix.UnixSensorsManager;
import com.profesorfalken.jsensors.manager.windows.WindowsSensorsManager;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.util.OSDetector;
import java.util.Map;

/**
 *
 * @author javier
 */
enum SensorsLocator {
    get;
    
    Components getComponents(Map<String, String> config) {
        return getManager(config).getComponents();
    }    
    
    private static SensorsManager getManager(Map<String, String> config) {
        boolean debugMode = false;
        if ("true".equals(config.get("debugMode"))) {
            debugMode = true;
        }
        
        if ("STUB".equals(config.get("testMode"))) {
            return new StubSensorsManager(config.get("stubContent")).debugMode(debugMode);
        }
        
        if (OSDetector.isWindows()) {
            return new WindowsSensorsManager().debugMode(debugMode);
        } else if (OSDetector.isUnix()) {
            return new UnixSensorsManager().debugMode(debugMode);
        }
        throw new UnsupportedOperationException(
                "Sorry, but your Operating System is not supported by jSensors");
    }
}
