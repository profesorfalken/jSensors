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
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.model.components.Components;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * A manager can now be created and reused, which avoids paying the backend initialisation
 * on every call (issue #13).
 *
 * @author Javier Garcia Alonso
 */
public class SensorsLocatorTest {

	private static Map<String, String> stubConfig() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("testMode", "STUB");
		config.put("stubContent", "[COMPONENT]\nCPU\nLabel: FakeCPU\nTemp temp1: 42.0\n");
		return config;
	}

	@Test
	public void testManagerCanBeObtainedAndReused() {
		SensorsManager manager = SensorsLocator.get.getManager(stubConfig());
		Assert.assertNotNull("No manager for this platform", manager);

		Components first = manager.getComponents();
		Components second = manager.getComponents();

		Assert.assertEquals(1, first.cpus.size());
		Assert.assertEquals(1, second.cpus.size());
		Assert.assertEquals("FakeCPU", second.cpus.get(0).name);
		Assert.assertEquals(42.0, second.cpus.get(0).sensors.temperatures.get(0).value, 0.0001);
	}
}
