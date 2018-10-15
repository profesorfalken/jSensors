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
package com.profesorfalken.jsensors.manager.unix;

import com.profesorfalken.jsensors.manager.unix.jna.CSubFeature;
import com.profesorfalken.jsensors.manager.unix.jna.CChip;
import com.profesorfalken.jsensors.manager.unix.jna.CFeature;
import com.profesorfalken.jsensors.manager.unix.jna.CSensors;
import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.util.SensorsUtils;
import com.sun.jna.Native;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Linux implementation of SensorManager that gets the sensors using JNA and
 * parses it into a normalized format.
 *
 * @author Javier Garcia Alonso
 */
public class UnixSensorsManager extends SensorsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnixSensorsManager.class);

	private static final String LINE_BREAK = "\n";

	private final StringBuilder sensorsData = new StringBuilder();
	private final StringBuilder sensorsDebugData = new StringBuilder();

	@Override
	public String getSensorsData() {
		CSensors cSensors = loadDynamicLibrary();

		if (cSensors == null) {
			LOGGER.error("Could not load sensors dynamic library");
			return "";
		}

		int init = initCSensors(cSensors);
		if (init != 0) {
			LOGGER.error("Cannot initialize sensors");
			return "";
		}

		String normalizedData = "";
		try {
			normalizedData = normalizeSensorsData(cSensors);
		} finally {
			cSensors.sensors_cleanup();
		}
		
		return normalizedData;
	}

	private CSensors loadDynamicLibrary() {
		Object jnaProxy;

		try {
			jnaProxy = Native.loadLibrary("sensors", CSensors.class);
		} catch (UnsatisfiedLinkError err) {
			LOGGER.info("Cannot find library in system, using embedded one");
			try {
				String libPath = SensorsUtils.generateLibTmpPath("/lib/linux/", "libsensors.so.4.4.0");
				jnaProxy = Native.loadLibrary(libPath, CSensors.class);
				new File(libPath).delete();
			} catch (UnsatisfiedLinkError err1) {
				jnaProxy = null;
				LOGGER.error("Cannot load sensors dinamic library", err1);
			}
		}

		return (CSensors) jnaProxy;
	}

	private static int initCSensors(CSensors cSensors) {
		return cSensors.sensors_init(null);
	}

	private void addData(String data) {
		addData(data, true);
	}

	private void addData(String data, boolean newLine) {
		String endLine = newLine ? LINE_BREAK : "";
		sensorsData.append(data).append(endLine);
		sensorsDebugData.append(data).append(endLine);
	}

	private void addDebugData(String debugData) {
		sensorsDebugData.append(debugData).append(LINE_BREAK);
	}

	private String normalizeSensorsData(CSensors cSensors) {

		List<CChip> chips = detectedChips(cSensors);

		for (final CChip chip : chips) {
			addData("[COMPONENT]");

			addDebugData(String.format("Type: %d", chip.bus.type));
			addDebugData(String.format("Address: %d", chip.addr));
			addDebugData(String.format("Path: %s", chip.path));
			addDebugData(String.format("Prefix: %s", chip.prefix));

			if (chip.bus != null) {
				switch (chip.bus.type) {
				case 1:
					addData("CPU");
					break;
				case 2:
					addData("GPU");
					break;
				case 4:
				case 5:
					addData("DISK");
					break;
				default:
					addData("UNKNOWN");
				}

			}

			addData(String.format("Label: %s", cSensors.sensors_get_adapter_name(chip.bus)));

			List<CFeature> features = features(cSensors, chip);

			addFeatures(cSensors, chip, features);
		}

		if (debugMode) {
			LOGGER.info(sensorsDebugData.toString());
		}

		return sensorsData.toString();
	}

	private void addFeatures(CSensors cSensors, CChip chip, List<CFeature> features) {
		for (final CFeature feature : features) {
			addDebugData(String.format("Feature type: %d", feature.type));
			addDebugData(String.format("Feature name: %s", feature.name));
			addDebugData(String.format("Feature label: %s", cSensors.sensors_get_label(chip, feature)));

			if (feature.name.startsWith("temp")) {
				addData(String.format("Temp %s:", cSensors.sensors_get_label(chip, feature)), false);
			} else if (feature.name.startsWith("fan")) {
				addData(String.format("Fan %s:", cSensors.sensors_get_label(chip, feature)), false);
			}

			List<CSubFeature> subFeatures = subFeatures(cSensors, chip, feature);

			addSubFeatures(cSensors, chip, subFeatures);
		}
	}

	private void addSubFeatures(CSensors cSensors, CChip chip, List<CSubFeature> subFeatures) {
		for (final CSubFeature subFeature : subFeatures) {
			addDebugData(String.format("SubFeature type: %d", subFeature.type));
			addDebugData(String.format("SubFeature name: %s", subFeature.name));

			double value = 0.0;
			DoubleByReference pValue = new DoubleByReference(value);
			if (cSensors.sensors_get_value(chip, subFeature.number, pValue) == 0) {
				addDebugData(String.format("SubFeature value: %s", pValue.getValue()));

				if (subFeature.name.endsWith("_input")) {
					addData(String.format("%s", pValue.getValue()));
					break;
				}
			} else {
				addData("Could not retrieve value");
			}
		}
	}

	private static List<CChip> detectedChips(CSensors cSensors) {
		List<CChip> detectedChips = new ArrayList<CChip>();

		CChip foundChip;
		int numSensor = 0;
		while ((foundChip = cSensors.sensors_get_detected_chips(null, new IntByReference(numSensor))) != null) {
			detectedChips.add(foundChip);
			numSensor++;
		}

		return detectedChips;
	}

	private static List<CFeature> features(CSensors cSensors, CChip chip) {
		List<CFeature> features = new ArrayList<CFeature>();

		CFeature foundFeature;
		int numFeature = 0;
		while ((foundFeature = cSensors.sensors_get_features(chip, new IntByReference(numFeature))) != null) {
			features.add(foundFeature);
			numFeature++;
		}

		return features;
	}

	private static List<CSubFeature> subFeatures(CSensors cSensors, CChip chip, CFeature feature) {
		List<CSubFeature> subFeatures = new ArrayList<CSubFeature>();
		CSubFeature foundSubFeature;
		int numSubFeature = 0;
		while ((foundSubFeature = cSensors.sensors_get_all_subfeatures(chip, feature,
				new IntByReference(numSubFeature))) != null) {
			subFeatures.add(foundSubFeature);
			numSubFeature++;
		}

		return subFeatures;
	}

}
