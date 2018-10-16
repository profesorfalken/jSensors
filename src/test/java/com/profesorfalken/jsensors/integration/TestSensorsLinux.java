/*
 * Copyright 2016-2018 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.integration;

import com.profesorfalken.jsensors.manager.unix.jna.CSubFeature;
import com.profesorfalken.jsensors.manager.unix.jna.CChip;
import com.profesorfalken.jsensors.manager.unix.jna.CFeature;
import com.profesorfalken.jsensors.manager.unix.jna.CSensors;
import com.sun.jna.Native;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;

/**
 *
 * @author javier
 */
public class TestSensorsLinux {

	public static void main(String[] args) {
		CSensors INSTANCE = Native.loadLibrary("sensors", CSensors.class);

		System.err.println("Return method: " + INSTANCE.sensors_init(null));

		CChip result;
		int numSensor = 0;
		while ((result = INSTANCE.sensors_get_detected_chips(null, new IntByReference(numSensor))) != null) {
			// System.out.println("Found " + result);
			numSensor++;
			System.out.println("Adapter " + INSTANCE.sensors_get_adapter_name(result.bus));

			CFeature feature;
			int numFeature = 0;
			while ((feature = INSTANCE.sensors_get_features(result, new IntByReference(numFeature))) != null) {
				// System.out.println("Found " + feature);
				numFeature++;

				String label = INSTANCE.sensors_get_label(result, feature);

				CSubFeature subFeature;
				int numSubFeature = 0;
				while ((subFeature = INSTANCE.sensors_get_all_subfeatures(result, feature,
						new IntByReference(numSubFeature))) != null) {
					double value = 0.0;
					DoubleByReference pValue = new DoubleByReference(value);

					int returnValue = INSTANCE.sensors_get_value(result, subFeature.number, pValue);
					System.out.println(label + " feature " + subFeature.name + ": " + pValue.getValue());
					System.out.println(label + "returnValue: " + returnValue);
					System.out.println();

					numSubFeature++;
				}
			}
		}

	}
}
