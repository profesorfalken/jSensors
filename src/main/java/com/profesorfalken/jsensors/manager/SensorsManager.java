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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.profesorfalken.jsensors.model.components.*;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Sensors;
import com.profesorfalken.jsensors.model.sensors.Temperature;

/**
 *
 * @author Javier Garcia Alonso
 */
public abstract class SensorsManager {

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
			}
		}

		return new Components(cpus, gpus, disks, mobos);
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

	private static String getName(String componentData) {
		String name = null;

		String[] dataLines = componentData.split("\\r?\\n");
		for (final String dataLine : dataLines) {
			if (dataLine.startsWith("Label")) {
				name = dataLine.split(":")[1].trim();
				break;
			}
		}

		return name;
	}

	private Sensors getSensors(String componentData) {
		List<Temperature> temperatures = new ArrayList<Temperature>();
		List<Fan> fans = new ArrayList<Fan>();
		List<Load> loads = new ArrayList<Load>();
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

		String[] dataLines = componentData.split("\\r?\\n");

		for (final String dataLine : dataLines) {
			try {
				if (dataLine.startsWith("Temp")) {
					String[] data = dataLine.split(":");

					Temperature temperature = new Temperature(data[0].trim(),
							data[1].trim().length() > 0 ? nf.parse(data[1].trim()).doubleValue() : 0.0);
					temperatures.add(temperature);
				} else if (dataLine.startsWith("Fan")) {
					String[] data = dataLine.split(":");
					Fan fan = new Fan(data[0].trim(),
							data[1].trim().length() > 0 ? nf.parse(data[1].trim()).doubleValue() : 0.0);
					fans.add(fan);
				} else if (dataLine.startsWith("Load")) {
					String[] data = dataLine.split(":");
					Load load = new Load(data[0].trim(),
							data[1].trim().length() > 0 ? nf.parse(data[1].trim()).doubleValue() : 0.0);
					loads.add(load);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return new Sensors(temperatures, fans, loads);
	}
}
