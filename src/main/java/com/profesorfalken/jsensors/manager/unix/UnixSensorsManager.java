/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.unix;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author javier
 */
public class UnixSensorsManager extends SensorsManager {

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

    private int initCSensors(CSensors cSensors) {
        return cSensors.sensors_init(null);
    }

    private String normalizeSensorsData(CSensors cSensors) {
        StringBuilder sensorsData = new StringBuilder();
        List<CChip> chips = detectedChips(cSensors);

        for (final CChip chip : chips) {
            sensorsData.append(chip);
            
            List<CFeature> features = features(cSensors, chip);
            
            for (final CFeature feature : features) {
                sensorsData.append(feature);
                
                List<CSubFeature> subFeatures = subFeatures(cSensors, chip, feature);
                for (final CSubFeature subFeature : subFeatures) {
                    sensorsData.append(subFeature);
                }
            }
        }
        
        System.out.println(sensorsData.toString());

        return sensorsData.toString();
    }

    private List<CChip> detectedChips(CSensors cSensors) {
        List<CChip> detectedChips = new ArrayList<CChip>();

        CChip foundChip;
        int numSensor = 0;
        while ((foundChip = cSensors.sensors_get_detected_chips(null, new IntByReference(numSensor))) != null) {
            detectedChips.add(foundChip);
        }

        return detectedChips;
    }

    private List<CFeature> features(CSensors cSensors, CChip chip) {
        List<CFeature> features = new ArrayList<CFeature>();

        CFeature foundFeature;
        int numFeature = 0;
        while ((foundFeature = cSensors.sensors_get_features(chip, new IntByReference(numFeature))) != null) {
            features.add(foundFeature);
        }

        return features;
    }

    private List<CSubFeature> subFeatures(CSensors cSensors, CChip chip, CFeature feature) {
        List<CSubFeature> subFeatures = new ArrayList<CSubFeature>();
        CSubFeature foundSubFeature;
        int numSubFeature = 0;
        while ((foundSubFeature = cSensors.sensors_get_all_subfeatures(chip, feature, new IntByReference(numSubFeature))) != null) {
            subFeatures.add(foundSubFeature);
        }

        return subFeatures;
    }

}
