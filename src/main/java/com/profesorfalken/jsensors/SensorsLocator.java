/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.unix.UnixSensorsManager;
import com.profesorfalken.jsensors.model.Components;
import com.profesorfalken.jsensors.util.OSDetector;

/**
 *
 * @author javier
 */
enum SensorsLocator {
    get;
    
    Components getComponents() {
        return getManager().getComponents();
    }
    
    Components getComponents(SensorsManager manager) {
        return manager.getComponents();
    }
    
    private static SensorsManager getManager() {
        if (OSDetector.isWindows()) {
            return null;
        } else if (OSDetector.isUnix()) {
            return new UnixSensorsManager();
        }
        throw new UnsupportedOperationException(
                "Sorry, but your Operating System is not supported by jSensors");
    }
}
