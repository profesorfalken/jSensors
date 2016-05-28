/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.unix;

import com.sun.jna.Native;
import java.util.List;

/**
 *
 * @author javier
 */
public class UnixSensorsManager {        
    public String getSensorsData() {
        CSensors cSensors = loadDynamicLibrary();
        if (cSensors == null) {
            //TODO: handle
        }
        
        int init = initCSensors(cSensors);
        if (init != 0) {
            //TODO: handle
        }
        
        return normalizeSensorsData(cSensors);
    }
    
    private CSensors loadDynamicLibrary() {
        return (CSensors) Native.loadLibrary("sensors",
                CSensors.class);
    }
    
    private int initCSensors (CSensors cSensors) {
        return cSensors.sensors_init(null);
    }
    
    private String normalizeSensorsData(CSensors cSensors) {
        StringBuilder sensorsData = new StringBuilder();        
        List<CChip> chips = detectedChips(cSensors);

        for (final CChip chip : chips) {
            sensorsData.append(chip);
        }
        
        return sensorsData.toString();
    }
    
    private List<CChip> detectedChips(CSensors cSensors) {
        return null;
    }
    
    private List<CFeature> features(CSensors cSensors, CChip chip) {
        return null;
    }   
    
    private List<CSubFeature> subFeatures(CSensors cSensors, CChip chip, CFeature feature) {
        return null;
    }   
    
}
