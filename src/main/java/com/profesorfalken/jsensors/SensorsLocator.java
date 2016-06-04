/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.stub.StubSensorsManager;
import com.profesorfalken.jsensors.manager.unix.UnixSensorsManager;
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
        if ("STUB".equals(config.get("testMode"))) {
            return new StubSensorsManager(config.get("stubContent"));
        }
        
        if (OSDetector.isWindows()) {
            return null;
        } else if (OSDetector.isUnix()) {
            return new UnixSensorsManager();
        }
        throw new UnsupportedOperationException(
                "Sorry, but your Operating System is not supported by jSensors");
    }
}
