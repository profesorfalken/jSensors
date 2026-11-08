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
}
