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

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/**
 * System CPU load, read from the operating system through the JMX bean.
 * <p>
 * On Linux and macOS the sensors backends only report temperature and fan speed: the
 * library never produced a {@code Load} sensor there, which is what issue #32 was about.
 * The JMX bean reports the load on every platform without reading {@code /proc} or
 * depending on a native library.
 *
 * @author Javier Garcia Alonso
 */
public final class SystemLoad {

	private SystemLoad() {
	}

	/**
	 * Returns the system CPU load as a percentage.
	 *
	 * @return load between 0 and 100, or -1 when the platform does not report it
	 */
	public static double cpuLoad() {
		try {
			java.lang.management.OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
			if (bean instanceof OperatingSystemMXBean) {
				double load = ((OperatingSystemMXBean) bean).getSystemCpuLoad();
				if (load >= 0) {
					return load * 100.0;
				}
			}
		} catch (RuntimeException | LinkageError ex) {
			// Not available on this JVM or platform: the caller simply omits the sensor
			return -1;
		}
		return -1;
	}
}
