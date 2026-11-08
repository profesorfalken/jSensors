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
package com.profesorfalken.jsensors.manager;

import java.util.ArrayList;
import java.util.List;

import com.profesorfalken.jsensors.model.components.*;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Sensors;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
public abstract class SensorsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorsManager.class);

	protected boolean debugMode = false;

	public SensorsManager debugMode(boolean mode) {
		this.debugMode = mode;
		return this;
	}

	protected abstract String getSensorsData();

	public Components getComponents() {
		List<Cpu> cpus = new ArrayList<Cpu>();
		List<Gpu> gpus = new ArrayList<Gpu>();
		List<Disk> disks = new ArrayList<Disk>();
		List<Mobo> mobos = new ArrayList<Mobo>();
		List<Other> others = new ArrayList<Other>();

		String normalizedSensorsData = getSensorsData();

		String[] componentsData = normalizedSensorsData.split("\\[COMPONENT\\]\\r?\\n");

		for (final String componentData : componentsData) {
			if (componentData.startsWith("CPU")) {
				cpus.add(getCpu(componentData));
			} else if (componentData.startsWith("GPU")) {
				gpus.add(getGpu(componentData));
			} else if (componentData.startsWith("DISK")) {
				disks.add(getDisk(componentData));
			} else if (componentData.startsWith("MOBO")) {
				mobos.add(getMobo(componentData));
			} else if (componentData.startsWith("OTHER")) {
				others.add(getOther(componentData));
			}
		}

		return new Components(cpus, gpus, disks, mobos, others);
	}

	private Cpu getCpu(String cpuData) {
		return new Cpu(getName(cpuData), getSensors(cpuData));
	}

	private Gpu getGpu(String gpuData) {
		return new Gpu(getName(gpuData), getSensors(gpuData));
	}

	private Disk getDisk(String diskData) {
		return new Disk(getName(diskData), getSensors(diskData));
	}

	private Mobo getMobo(String moboData) {
		return new Mobo(getName(moboData), getSensors(moboData));
	}

	private Other getOther(String otherData) {
		return new Other(getName(otherData), getSensors(otherData));
	}

	private static String getName(String componentData) {
		String[] dataLines = componentData.split("\\r?\\n");
		for (final String dataLine : dataLines) {
			if (dataLine.startsWith("Label")) {
				String[] data = dataLine.split(":", 2);
				if (data.length > 1) {
					return data[1].trim();
				}
			}
		}

		return null;
	}

	private Sensors getSensors(String componentData) {
		List<Temperature> temperatures = new ArrayList<Temperature>();
		List<Fan> fans = new ArrayList<Fan>();
		List<Load> loads = new ArrayList<Load>();

		String[] dataLines = componentData.split("\\r?\\n");

		for (final String dataLine : dataLines) {
			String kind;
			if (dataLine.startsWith("Temp")) {
				kind = "temperature";
			} else if (dataLine.startsWith("Fan")) {
				kind = "fan";
			} else if (dataLine.startsWith("Load")) {
				kind = "load";
			} else {
				continue;
			}

			String[] data = dataLine.split(":", 2);
			if (data.length < 2) {
				continue;
			}

			String sensorName = data[0].trim();
			String rawValue = data[1].trim();

			if (rawValue.isEmpty()) {
				continue;
			}

			double value;
			try {
				value = parseValue(rawValue);
			} catch (NumberFormatException nfe) {
				// A sensor that cannot be parsed is skipped instead of printing a stack trace
				LOGGER.warn("Cannot parse {} value '{}'", kind, rawValue);
				continue;
			}

			if ("temperature".equals(kind)) {
				temperatures.add(new Temperature(sensorName, value));
			} else if ("fan".equals(kind)) {
				fans.add(new Fan(sensorName, value));
			} else {
				loads.add(new Load(sensorName, value));
			}
		}

		return new Sensors(temperatures, fans, loads);
	}

	/**
	 * Parses a sensor value produced by any backend.
	 * <p>
	 * The previous implementation used {@code NumberFormat.getInstance(Locale.getDefault())},
	 * so the result depended on the machine locale: in Spanish or German, "80.0" was parsed
	 * as 800 and "30.85" as 3085, because the dot is the grouping separator there. Sensor
	 * values are always written with a dot, but a comma may appear when PowerShell formats
	 * a number with a decimal-comma culture, so both are accepted.
	 *
	 * @param rawValue value as written by the backend
	 * @return numeric value
	 * @throws NumberFormatException when the value contains no number
	 */
	static double parseValue(String rawValue) {
		String value = rawValue.trim().replace(',', '.');

		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException firstAttempt) {
			// Units or extra text around the number, e.g. "33.85 C"
			String digits = value.replaceAll("[^0-9.\\-]", "");
			if (digits.isEmpty()) {
				throw firstAttempt;
			}
			return Double.parseDouble(digits);
		}
	}
}
