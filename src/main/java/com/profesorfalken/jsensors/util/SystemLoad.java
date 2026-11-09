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
	 * Time to wait before taking a second sample. The first sample only establishes the
	 * reference point, so it always reports 0 and would show an idle CPU on the first call.
	 */
	private static final int PRIMING_MILLISECONDS = 120;

	private static boolean primed = false;

	/**
	 * Returns the system CPU load as a percentage.
	 * <p>
	 * The first call primes the sample: the operating system needs a reference point to
	 * compute the load, so without it the first reading is always 0. It costs
	 * {@value #PRIMING_MILLISECONDS} ms once per JVM; later calls use the interval since
	 * the previous one.
	 *
	 * @return load between 0 and 100, or -1 when the platform does not report it
	 */
	public static synchronized double cpuLoad() {
		double load = readLoad();

		if (!primed) {
			primed = true;
			if (load <= 0) {
				try {
					Thread.sleep(PRIMING_MILLISECONDS);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					return load;
				}
				load = readLoad();
			}
		}

		return load;
	}

	private static double readLoad() {
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
