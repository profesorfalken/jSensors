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
package com.profesorfalken.jsensors.unit;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Configuration behaviour of the public API.
 *
 * @author Javier Garcia Alonso
 */
public class JSensorsConfigTest {

	private static final String STUB = "[COMPONENT]\n"
			+ "CPU\n"
			+ "Label: FakeCPU\n"
			+ "Temp Package: 80.0\n"
			+ "Fan cpu_fan: 1500,5\n";

	@After
	public void closeSessions() {
		// On Windows the real mode keeps a PowerShell session open
		JSensors.get.close();
	}

	/**
	 * End to end check of the locale fix, with a decimal comma in the value.
	 */
	@Test
	public void testStubParsingWithDecimalComma() {
		Locale previous = Locale.getDefault();
		try {
			Locale.setDefault(new Locale("es", "ES"));

			Components components = JSensors.get.config(stubConfig()).components();

			Assert.assertEquals(1, components.cpus.size());
			Assert.assertEquals(80.0, components.cpus.get(0).sensors.temperatures.get(0).value, 0.0001);
			Assert.assertEquals(1500.5, components.cpus.get(0).sensors.fans.get(0).value, 0.0001);
		} finally {
			Locale.setDefault(previous);
		}
	}

	/**
	 * Overrides passed to config() only apply to the next call. They used to be written
	 * into the defaults, so every later call kept using them.
	 */
	@Test
	public void testConfigurationDoesNotLeakBetweenCalls() {
		JSensors.get.config(stubConfig()).components();

		Components next = JSensors.get.components();
		boolean stillStubbed = !next.cpus.isEmpty() && "FakeCPU".equals(next.cpus.get(0).name);

		Assert.assertFalse("The stub configuration leaked to the next call", stillStubbed);
	}

	private static Map<String, String> stubConfig() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("testMode", "STUB");
		config.put("stubContent", STUB);
		return config;
	}
}
