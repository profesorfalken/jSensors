/*
 * Copyright 2016-2018 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MS Windows implementation of SensorManager that gets the sensors using a
 * PowerShell script and parses it into a normalized format.
 *
 * @author Javier Garcia Alonso
 */
public class WindowsSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSensorsManager.class);

    private static final String LINE_BREAK = "\r\n";
    
    private static final String COMPONENT_SEPARATOR = "[COMPONENT]";

    @Override
    public String getSensorsData() {

        String rawSensorsData = PowerShellOperations.GET.getRawSensorsData();

        if (debugMode) {
            LOGGER.info("RawSensorData: " + rawSensorsData);
        }

        String normalizedSensorsData = normalizeSensorsData(rawSensorsData);
        if (debugMode) {
            LOGGER.info("NormalizeSensorData: " + normalizedSensorsData);
        }

        return normalizedSensorsData;
    }

    private static String normalizeSensorsData(String rawSensorsData) {
        StringBuilder normalizedSensorsData = new StringBuilder();
        String[] dataLines = rawSensorsData.split("\\r?\\n");

        boolean readingHardLabel = false;
        boolean readingSensor = false;
        for (final String dataLine : dataLines) {
            if (!readingHardLabel && "HardwareType".equals(getKey(dataLine))) {
                String hardwareType = getValue(dataLine);
                String componentType = componentType(hardwareType);
                if (componentType != null) {
                    normalizedSensorsData.append(COMPONENT_SEPARATOR).append(LINE_BREAK);
                    normalizedSensorsData.append(componentType).append(LINE_BREAK);
                    // A motherboard has no Name line, so it must not wait for one
                    readingHardLabel = !"MOBO".equals(componentType);
                }
                continue;
            }

            if (readingHardLabel) {
                if ("Name".equals(getKey(dataLine))) {
                    normalizedSensorsData.append("Label: ").append(getValue(dataLine)).append(LINE_BREAK);
                    readingHardLabel = false;
                }
            } else {
                readingSensor = addSensorsData(readingSensor, dataLine, normalizedSensorsData);
            }
        }

        return normalizedSensorsData.toString();
    }

    private static boolean addSensorsData(boolean readingSensor, String dataLine, StringBuilder normalizedSensorsData) {
        if ("SensorType".equals(getKey(dataLine))) {
            String sensorType = getValue(dataLine);
            if ("Temperature".equals(sensorType)) {
            	normalizedSensorsData.append("Temp ");
            	return true;
            } else if ("Fan".equals(sensorType)) {
            	normalizedSensorsData.append("Fan ");
            	return true;
            } else if ("Load".equals(sensorType)) {
            	normalizedSensorsData.append("Load ");
            	return true;
            }
        }
        if (readingSensor) {
            if ("Name".equals(getKey(dataLine))) {
                normalizedSensorsData.append(getValue(dataLine)).append(": ");
                return true;
            } else if ("Value".equals(getKey(dataLine))) {
                normalizedSensorsData.append(getValue(dataLine)).append(LINE_BREAK);
                return false;
            } else {
            	return true;
            }
        }
        return false;
    }

    /**
     * Maps an OpenHardwareMonitor {@code HardwareType} to a component type.
     * <p>
     * The comparison is case insensitive and accepts the aliases found across
     * OpenHardwareMonitor versions: the enum says {@code Storage} and
     * {@code Motherboard}, but older releases used {@code Hdd} and {@code Mainboard}, and
     * the previous code compared against {@code "HDD"} and {@code "Mainboard"} with
     * {@code equals}, so disks and motherboards were never reported on Windows.
     * <p>
     * Types that do not fit the model (memory, network, battery...) are reported as
     * {@code OTHER} instead of being dropped.
     *
     * @param hardwareType value of the HardwareType property
     * @return CPU, GPU, DISK, MOBO, OTHER or null when there is no type
     */
    static String componentType(String hardwareType) {
        if (hardwareType == null || hardwareType.trim().isEmpty()) {
            return null;
        }

        String type = hardwareType.trim();

        if ("Cpu".equalsIgnoreCase(type)) {
            return "CPU";
        }
        if (type.toUpperCase(Locale.ROOT).startsWith("GPU")) {
            return "GPU";
        }
        if ("Storage".equalsIgnoreCase(type) || "Hdd".equalsIgnoreCase(type)) {
            return "DISK";
        }
        if ("Motherboard".equalsIgnoreCase(type) || "Mainboard".equalsIgnoreCase(type)) {
            return "MOBO";
        }

        return "OTHER";
    }

    private static String getKey(String line) {
        return getData(line, 0);
    }

    private static String getValue(String line) {
        return getData(line, 1);
    }

    private static String getData(String line, final int index) {
        if (line.contains(":")) {
            return line.split(":", 2)[index].trim();
        }

        return "";
    }
}
