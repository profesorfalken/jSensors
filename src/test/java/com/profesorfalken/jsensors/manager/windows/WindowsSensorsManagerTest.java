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
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.stub.StubSensorsManager;
import com.profesorfalken.jsensors.model.components.Components;
import org.junit.Assert;
import org.junit.Test;

/**
 * Mapping of the OpenHardwareMonitor HardwareType values.
 * <p>
 * The tests use the values of the actual enum, so they run on any platform instead of
 * depending on a Windows machine to be verified.
 *
 * @author Javier Garcia Alonso
 */
public class WindowsSensorsManagerTest {

	@Test
	public void testCpu() {
		Assert.assertEquals("CPU", WindowsSensorsManager.componentType("Cpu"));
	}

	@Test
	public void testGpuVariants() {
		Assert.assertEquals("GPU", WindowsSensorsManager.componentType("GpuNvidia"));
		Assert.assertEquals("GPU", WindowsSensorsManager.componentType("GpuAti"));
		Assert.assertEquals("GPU", WindowsSensorsManager.componentType("GpuIntel"));
	}

	/**
	 * The enum says Storage; older versions used Hdd. The code used to compare against
	 * "HDD" with equals, so disks were never reported.
	 */
	@Test
	public void testDiskAliases() {
		Assert.assertEquals("DISK", WindowsSensorsManager.componentType("Storage"));
		Assert.assertEquals("DISK", WindowsSensorsManager.componentType("Hdd"));
	}

	/**
	 * The enum says Motherboard; the code compared against "Mainboard".
	 */
	@Test
	public void testMotherboardAliases() {
		Assert.assertEquals("MOBO", WindowsSensorsManager.componentType("Motherboard"));
		Assert.assertEquals("MOBO", WindowsSensorsManager.componentType("Mainboard"));
	}

	/**
	 * Types that do not fit the model are reported instead of dropped.
	 */
	@Test
	public void testOtherTypesAreKept() {
		Assert.assertEquals("OTHER", WindowsSensorsManager.componentType("Memory"));
		Assert.assertEquals("OTHER", WindowsSensorsManager.componentType("Network"));
		Assert.assertEquals("OTHER", WindowsSensorsManager.componentType("Battery"));
		Assert.assertEquals("OTHER", WindowsSensorsManager.componentType("SuperIO"));
	}

	@Test
	public void testEmptyType() {
		Assert.assertNull(WindowsSensorsManager.componentType(""));
		Assert.assertNull(WindowsSensorsManager.componentType(null));
	}

	/**
	 * Raw output as printed by OpenHardwareMonitor 0.9.6 on Windows, trimmed. Note the
	 * order: the motherboard prints Name before HardwareType, the CPU after.
	 */
	private static final String RAW = String.join("\n",
			"Name         : Dell 08NMJP",
			"Identifier   : /mainboard",
			"HardwareType : Mainboard",
			"Parent       : ",
			"Sensors      : {}",
			"",
			"HardwareType              : CPU",
			"Name                      : Intel Core Ultra 7 255H",
			"Identifier                : /intelcpu/0",
			"Sensors                   : {CPU Total}",
			"",
			"Hardware        : OpenHardwareMonitor.Hardware.CPU.IntelCPU",
			"SensorType      : Load",
			"Name            : CPU Total",
			"Value           : 18,6452",
			"",
			"HardwareType              : HDD",
			"Name                      : Generic Hard Disk",
			"Identifier                : /hdd/0",
			"",
			"HardwareType              : RAM",
			"Name                      : Generic Memory",
			"Identifier                : /ram/0",
			"");

	/**
	 * The motherboard name is printed before its HardwareType, so the parser used to
	 * report it with a null name.
	 */
	@Test
	public void testHardwareNameBeforeAndAfterTheType() {
		// The Windows backend separates lines with CRLF
		String normalized = WindowsSensorsManager.normalizeSensorsData(RAW).replace("\r\n", "\n");

		Assert.assertTrue("Motherboard name lost: " + normalized,
				normalized.contains("MOBO\nLabel: Dell 08NMJP"));
		Assert.assertTrue("CPU name lost: " + normalized,
				normalized.contains("CPU\nLabel: Intel Core Ultra 7 255H"));
	}

	/**
	 * End to end: the normalized output is parsed into components, including the values
	 * written with a decimal comma.
	 */
	@Test
	public void testNormalizedOutputIsParsed() {
		String normalized = WindowsSensorsManager.normalizeSensorsData(RAW);
		SensorsManager manager = new StubSensorsManager(normalized);
		Components components = manager.getComponents();

		Assert.assertEquals(1, components.mobos.size());
		Assert.assertEquals("Dell 08NMJP", components.mobos.get(0).name);

		Assert.assertEquals(1, components.cpus.size());
		Assert.assertEquals("Intel Core Ultra 7 255H", components.cpus.get(0).name);
		Assert.assertEquals(18.6452, components.cpus.get(0).sensors.loads.get(0).value, 0.0001);

		Assert.assertEquals(1, components.disks.size());
		Assert.assertEquals("Generic Hard Disk", components.disks.get(0).name);

		// RAM is not one of the four component types: it must not be lost
		Assert.assertEquals(1, components.others.size());
		Assert.assertEquals("Generic Memory", components.others.get(0).name);
	}
}
