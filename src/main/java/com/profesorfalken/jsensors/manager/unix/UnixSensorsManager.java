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
import com.profesorfalken.jsensors.util.SystemLoad;
import com.sun.jna.Native;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

	private static final String COMPONENT_SEPARATOR = "[COMPONENT]";

	private final StringBuilder sensorsData = new StringBuilder();
	private final StringBuilder sensorsDebugData = new StringBuilder();

	@Override
	public String getSensorsData() {
		// The manager can be reused: never accumulate data from previous calls
		sensorsData.setLength(0);
		sensorsDebugData.setLength(0);

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
				if (libPath.isEmpty()) {
					return null;
				}
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
		sensorsData.append(data).append(LINE_BREAK);
		sensorsDebugData.append(data).append(LINE_BREAK);
	}

	private void addDebugData(String debugData) {
		sensorsDebugData.append(debugData).append(LINE_BREAK);
	}

	private String normalizeSensorsData(CSensors cSensors) {

		List<CChip> chips = detectedChips(cSensors);
		boolean loadReported = false;

		for (final CChip chip : chips) {
			String componentType = classifyComponent(chip);
			if (componentType == null) {
				// Unknown chips are reported instead of dropped: losing a sensor is worse
				// than filing it under "other"
				componentType = "OTHER";
				addDebugData(String.format("Unclassified chip: %s (bus type %s)", chipName(chip),
						chip.bus == null ? -1 : chip.bus.type));
			}

			addData(COMPONENT_SEPARATOR);
			addData(componentType);

			addDebugData(String.format("Type: %s", componentType));
			addDebugData(String.format("Address: %d", chip.addr));
			addDebugData(String.format("Path: %s", chip.path));
			addDebugData(String.format("Chip: %s", chip.prefix));

			addData(String.format("Label: %s", chipName(chip)));

			List<CFeature> features = features(cSensors, chip);

			addFeatures(cSensors, chip, features);

			// lm-sensors provides temperature and fan speed, but no CPU load: the JMX bean
			// covers that gap (see issue #32)
			if (!loadReported && "CPU".equals(componentType)) {
				double load = SystemLoad.cpuLoad();
				if (load >= 0) {
					addData(String.format(Locale.ROOT, "Load CPU Total: %.1f", load));
					loadReported = true;
				}
			}
		}

		if (debugMode) {
			LOGGER.info(sensorsDebugData.toString());
		}

		return sensorsData.toString();
	}

	/**
	 * Returns the component type for a chip, or null when the chip does not map to a
	 * supported component.
	 * <p>
	 * The bus type alone is not enough on modern hardware: an NVMe drive enumerates as a
	 * PCI device and used to be reported as a GPU, while ACPI thermal zones, batteries and
	 * WiFi chips enumerate on the ISA bus and used to be reported as CPUs. The chip name is
	 * the reliable signal, so only names that are actually understood are reported; chips
	 * that do not fit the model (battery, WiFi, USB-C power supply...) are skipped instead
	 * of being filed under an invented category.
	 *
	 * @param chip detected chip
	 * @return CPU, GPU, DISK, MOBO or null
	 */
	static String classifyComponent(CChip chip) {
		String name = chipName(chip).toLowerCase(Locale.ROOT);

		if (startsWithAny(name, "coretemp", "k10temp", "zenpower", "cpu_thermal", "soc_thermal", "cpu")) {
			return "CPU";
		}
		if (startsWithAny(name, "nvme", "drivetemp", "sd", "hd", "ata", "mmcblk")) {
			return "DISK";
		}
		if (startsWithAny(name, "amdgpu", "radeon", "nouveau", "nvidia", "i915", "gpu")) {
			return "GPU";
		}
		if (startsWithAny(name, "it87", "nct", "f7", "w83", "asus", "gigabyte", "dell_smm",
				"pch_", "acpitz", "thinkpad", "applesmc")) {
			return "MOBO";
		}

		return null;
	}

	private static boolean startsWithAny(String name, String... prefixes) {
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Name of the chip, which is what actually identifies a component: coretemp-isa-0000,
	 * nvme-pci-0100, amdgpu-pci-0100...
	 *
	 * @param chip detected chip
	 * @return chip name, falling back to the adapter name and finally to a generic label
	 */
	private static String chipName(CChip chip) {
		if (chip.prefix != null && !chip.prefix.trim().isEmpty()) {
			return chip.prefix.trim();
		}
		return "unknown";
	}

	private void addFeatures(CSensors cSensors, CChip chip, List<CFeature> features) {
		for (final CFeature feature : features) {
			addDebugData(String.format("Feature type: %d", feature.type));
			addDebugData(String.format("Feature name: %s", feature.name));

			String kind;
			if (feature.name.startsWith("temp")) {
				kind = "Temp";
			} else if (feature.name.startsWith("fan")) {
				kind = "Fan";
			} else {
				continue;
			}

			String label = cSensors.sensors_get_label(chip, feature);
			String sensorName = (label == null || label.trim().isEmpty()) ? feature.name : label.trim();

			Double value = readFeatureValue(cSensors, chip, feature);
			if (value == null) {
				addDebugData(String.format("No value for %s", sensorName));
				continue;
			}

			addData(String.format("%s %s: %s", kind, sensorName, value));
		}
	}

	/**
	 * Reads the value of a feature. Only a successful read produces a value: a failed
	 * read used to be written as the text "Could not retrieve value" into the numeric
	 * field, which then broke the parser.
	 *
	 * @param cSensors sensors library
	 * @param chip     chip the feature belongs to
	 * @param feature  feature to read
	 * @return the value, or null when it cannot be read
	 */
	private Double readFeatureValue(CSensors cSensors, CChip chip, CFeature feature) {
		for (final CSubFeature subFeature : subFeatures(cSensors, chip, feature)) {
			if (subFeature.name == null || !subFeature.name.endsWith("_input")) {
				continue;
			}
			DoubleByReference value = new DoubleByReference(0.0);
			if (cSensors.sensors_get_value(chip, subFeature.number, value) == 0) {
				addDebugData(String.format("SubFeature value: %s", value.getValue()));
				return value.getValue();
			}
			addDebugData(String.format("Cannot read subfeature %s", subFeature.name));
			return null;
		}
		return null;
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
