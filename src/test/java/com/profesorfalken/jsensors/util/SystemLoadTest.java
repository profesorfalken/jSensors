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
package com.profesorfalken.jsensors.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * The CPU load is read from the JMX bean, which is what covers the missing Load sensor
 * on Linux and macOS (issue #32).
 *
 * @author Javier Garcia Alonso
 */
public class SystemLoadTest {

	@Test
	public void testCpuLoadIsAValidPercentageOrUnavailable() {
		double load = SystemLoad.cpuLoad();

		Assert.assertFalse("CPU load is NaN", Double.isNaN(load));
		Assert.assertTrue("CPU load out of range: " + load, load == -1 || (load >= 0 && load <= 100));
	}
}
