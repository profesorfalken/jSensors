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
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.stub.StubSensorsManager;
import com.profesorfalken.jsensors.manager.unix.UnixSensorsManager;
import com.profesorfalken.jsensors.manager.windows.WindowsSensorsManager;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.util.OSDetector;
import java.util.Map;

/**
 *
 * @author Javier Garcia Alonso
 */
enum SensorsLocator {
	get;

	Components getComponents(Map<String, String> config) {
		return getManager(config).getComponents();
	}

	private static SensorsManager getManager(Map<String, String> config) {
		boolean debugMode = false;
		if ("true".equals(config.get("debugMode"))) {
			debugMode = true;
		}

		if ("STUB".equals(config.get("testMode"))) {
			return new StubSensorsManager(config.get("stubContent")).debugMode(debugMode);
		}

		if (OSDetector.isWindows()) {
			return new WindowsSensorsManager().debugMode(debugMode);
		} else if (OSDetector.isUnix()) {
			return new UnixSensorsManager().debugMode(debugMode);
		}
		throw new UnsupportedOperationException("Sorry, but your Operating System is not supported by jSensors");
	}
}
