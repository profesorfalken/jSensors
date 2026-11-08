/*
 * Copyright 2016-2018 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Component;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;

/**
 *
 * @author javier
 */
public class JSensorsIntegrationTest {

	@After
	public void tearDown() {
		// On Windows the real mode keeps a PowerShell session open
		JSensors.get.close();
	}

	/*
	 * Make a real call and check that the components are retrieved and plausible
	 */
	@Test
	public void testJSensorsRealReturn() {
		Map<String, String> config = new HashMap<String, String>();

		config.put("testMode", "REAL");

		Components components = JSensors.get.config(config).components();
		assertNotNull("Components is null", components);

		assertNotNull("CPU list is null", components.cpus);
		assertNotNull("GPU list is null", components.gpus);
		assertNotNull("Disk list is null", components.disks);
		assertNotNull("Mobo list is null", components.mobos);

		// The number of components depends on the machine, but whatever is reported has
		// to be a plausible reading: a locale dependent parse used to report 800 C
		for (Component cpu : components.cpus) {
			assertSaneValues(cpu);
		}
		for (Component gpu : components.gpus) {
			assertSaneValues(gpu);
		}
		for (Component disk : components.disks) {
			assertSaneValues(disk);
		}
		for (Component mobo : components.mobos) {
			assertSaneValues(mobo);
		}
	}

	private static void assertSaneValues(Component component) {
		assertNotNull("Component is null", component);

		List<Temperature> temperatures = component.sensors.temperatures;
		for (Temperature temperature : temperatures) {
			assertNotNull("Temperature value is null", temperature.value);
			assertTrue("Temperature out of range: " + temperature.name + " = " + temperature.value,
					temperature.value >= 0 && temperature.value < 150);
		}

		List<Fan> fans = component.sensors.fans;
		for (Fan fan : fans) {
			assertNotNull("Fan value is null", fan.value);
			assertTrue("Fan speed out of range: " + fan.name + " = " + fan.value,
					fan.value >= 0 && fan.value < 20000);
		}
	}
}
