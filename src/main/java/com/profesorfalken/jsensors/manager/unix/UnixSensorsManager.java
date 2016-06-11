/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.unix;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.manager.SensorsManager;
import com.sun.jna.Native;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public class UnixSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnixSensorsManager.class);

    private static final String LINE_BREAK = "\n";
    
    private StringBuilder sensorsData = new StringBuilder();
    private StringBuilder sensorsDebugData = new StringBuilder();

    public String getSensorsData() {
        CSensors cSensors = loadDynamicLibrary();
        if (cSensors == null) {
            LOGGER.error("Could not load sensors dynamic library");

            //TODO: handle
        }

        int init = initCSensors(cSensors);
        if (init != 0) {
            LOGGER.error("Cannot initialize sensors");

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
        
        List<CChip> chips = detectedChips(cSensors);

        for (final CChip chip : chips) {
            sensorsData.append("[COMPONENT]").append(LINE_BREAK);
            sensorsDebugData.append("Type: ").append(chip.bus.type).append(LINE_BREAK);
            sensorsDebugData.append("Address: ").append(chip.addr).append(LINE_BREAK);
            sensorsDebugData.append("Path: ").append(chip.path).append(LINE_BREAK);
            sensorsDebugData.append("Prefix: ").append(chip.prefix).append(LINE_BREAK);

            if (chip.bus != null) {
                switch (chip.bus.type) {
                    case 1:
                        sensorsData.append("CPU").append(LINE_BREAK);
                        break;
                    case 2:
                        sensorsData.append("GPU").append(LINE_BREAK);
                        break;
                    case 4:
                    case 5:
                        sensorsData.append("DISK").append(LINE_BREAK);
                        break;
                    default:
                        sensorsData.append("UNKNOWN").append(LINE_BREAK);
                }

            }
            sensorsData.append("Label: ")
                    .append(cSensors.sensors_get_adapter_name(chip.bus))
                    .append(LINE_BREAK);

            List<CFeature> features = features(cSensors, chip);

            for (final CFeature feature : features) {
                sensorsDebugData.append("Feature type: ").append(feature.type).append(LINE_BREAK);
                sensorsDebugData.append("Feature name: ").append(feature.name).append(LINE_BREAK);
                sensorsDebugData.append("Feature label: ").append(cSensors.sensors_get_label(chip, feature)).append(LINE_BREAK);
                
                if (feature.name.startsWith("temp")) {
                    sensorsData.append("Temp ").append(cSensors.sensors_get_label(chip, feature)).append(":");
                } else if (feature.name.startsWith("fan")) {
                    sensorsData.append("Fan ").append(cSensors.sensors_get_label(chip, feature)).append(":");
                }

                List<CSubFeature> subFeatures = subFeatures(cSensors, chip, feature);
                for (final CSubFeature subFeature : subFeatures) {
                    sensorsDebugData.append("SubFeature type: ").append(subFeature.type).append(LINE_BREAK);
                    sensorsDebugData.append("SubFeature name: ").append(subFeature.name).append(LINE_BREAK);

                    double value = 0.0;
                    DoubleByReference pValue = new DoubleByReference(value);
                    int returnValue = cSensors.sensors_get_value(chip, subFeature.number, pValue);
                    sensorsDebugData.append("SubFeature value: ").append(pValue.getValue()).append(LINE_BREAK);
                    
                    if (subFeature.name.endsWith("_input")) {
                        sensorsData.append(pValue.getValue()).append(LINE_BREAK);
                        break;
                    }
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
            numSensor++;
        }

        return detectedChips;
    }

    private List<CFeature> features(CSensors cSensors, CChip chip) {
        List<CFeature> features = new ArrayList<CFeature>();

        CFeature foundFeature;
        int numFeature = 0;
        while ((foundFeature = cSensors.sensors_get_features(chip, new IntByReference(numFeature))) != null) {
            features.add(foundFeature);
            numFeature++;
        }

        return features;
    }

    private List<CSubFeature> subFeatures(CSensors cSensors, CChip chip, CFeature feature) {
        List<CSubFeature> subFeatures = new ArrayList<CSubFeature>();
        CSubFeature foundSubFeature;
        int numSubFeature = 0;
        while ((foundSubFeature = cSensors.sensors_get_all_subfeatures(chip, feature, new IntByReference(numSubFeature))) != null) {
            subFeatures.add(foundSubFeature);
            numSubFeature++;
        }

        return subFeatures;
    }

}
