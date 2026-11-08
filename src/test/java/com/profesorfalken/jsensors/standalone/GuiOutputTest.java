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
package com.profesorfalken.jsensors.standalone;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Mobo;
import com.profesorfalken.jsensors.model.components.Other;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Sensors;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.table.DefaultTableModel;
import org.junit.Assert;
import org.junit.Test;

/**
 * The table model used to be built from a background thread and only included CPUs, GPUs
 * and disks, so fans living on the motherboard chip never reached the window.
 *
 * @author Javier Garcia Alonso
 */
public class GuiOutputTest {

	@Test
	public void testAllComponentTypesAreIncluded() {
		Cpu cpu = new Cpu("coretemp", sensors(new Temperature("Core 0", 55.0)));
		Mobo mobo = new Mobo("dell_smm", sensorsFans(new Fan("fan1", 3200.0)));
		Other other = new Other("BAT0", sensors(new Temperature("temp1", 31.0)));

		Components components = new Components(Collections.singletonList(cpu),
				Collections.<com.profesorfalken.jsensors.model.components.Gpu>emptyList(),
				Collections.<com.profesorfalken.jsensors.model.components.Disk>emptyList(),
				Collections.singletonList(mobo),
				Collections.singletonList(other));

		DefaultTableModel model = GuiOutput.buildModel(components);

		String content = dump(model);
		Assert.assertTrue("Missing CPU: " + content, content.contains("CPU component: coretemp"));
		Assert.assertTrue("Missing motherboard: " + content, content.contains("Motherboard component: dell_smm"));
		Assert.assertTrue("Missing other: " + content, content.contains("Other component: BAT0"));
		Assert.assertTrue("Fan sensor missing: " + content, content.contains("fan1: "));
	}

	private static String dump(DefaultTableModel model) {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < model.getRowCount(); row++) {
			sb.append(model.getValueAt(row, 0)).append(" | ").append(model.getValueAt(row, 1)).append('\n');
		}
		return sb.toString();
	}

	private static Sensors sensors(Temperature temperature) {
		return new Sensors(Collections.singletonList(temperature), Collections.<Fan>emptyList(),
				Collections.emptyList());
	}

	private static Sensors sensorsFans(Fan fan) {
		return new Sensors(Collections.<Temperature>emptyList(), Arrays.asList(fan), Collections.emptyList());
	}
}
