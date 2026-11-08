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
package com.profesorfalken.jsensors.manager.unix;

import com.profesorfalken.jsensors.manager.unix.jna.CChip;
import org.junit.Assert;
import org.junit.Test;

/**
 * Chips are classified by name. Classifying by bus type reported an NVMe drive as a GPU
 * and an ACPI thermal zone as a CPU.
 *
 * @author Javier Garcia Alonso
 */
public class UnixSensorsManagerTest {

	@Test
	public void testCpuChips() {
		Assert.assertEquals("CPU", UnixSensorsManager.classifyComponent(chip("coretemp-isa-0000")));
		Assert.assertEquals("CPU", UnixSensorsManager.classifyComponent(chip("k10temp-pci-00c3")));
	}

	@Test
	public void testDiskChips() {
		Assert.assertEquals("DISK", UnixSensorsManager.classifyComponent(chip("nvme-pci-0100")));
		Assert.assertEquals("DISK", UnixSensorsManager.classifyComponent(chip("drivetemp-scsi-0-0")));
	}

	@Test
	public void testGpuChips() {
		Assert.assertEquals("GPU", UnixSensorsManager.classifyComponent(chip("amdgpu-pci-0100")));
	}

	@Test
	public void testMotherboardChips() {
		Assert.assertEquals("MOBO", UnixSensorsManager.classifyComponent(chip("it8728-isa-0290")));
	}

	@Test
	public void testUnknownChipsAreNotInvented() {
		Assert.assertNull(UnixSensorsManager.classifyComponent(chip("BAT0-acpi-0")));
		Assert.assertNull(UnixSensorsManager.classifyComponent(chip("iwlwifi_1-virtual-0")));
	}

	private static CChip chip(String name) {
		CChip chip = new CChip();
		chip.prefix = name;
		return chip;
	}
}
