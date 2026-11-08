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

import java.util.HashMap;
import java.util.Map;

import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import com.profesorfalken.jsensors.util.OSDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.standalone.ConsoleOutput;
import com.profesorfalken.jsensors.standalone.GuiOutput;

/**
 * Main class of JSensors. <br>
 * As library, it provides access to sensors information using a fluent api.
 * <p>
 * If launched as standalone application it prints in console all the retrieved
 * sensor information.
 * 
 * @author Javier Garcia Alonso
 */
public enum JSensors {

	get;

	private static final Logger LOGGER = LoggerFactory.getLogger(JSensors.class);

	final Map<String, String> baseConfig;

	private Map<String, String> usedConfig = null;

	static {
		checkRights();
	}

	private static void checkRights() {
		try {
			if (OSDetector.isWindows() && !PowerShellOperations.isAdministrator()) {
				LOGGER.warn("You have not executed jSensors in Administrator mode, so CPU temperature sensors will not be detected.");
			}
		} catch (RuntimeException ex) {
			// A failed check must never break the class initialisation
			LOGGER.debug("Cannot check administrator rights", ex);
		}
	}

	JSensors() {
		// Load config from file
		baseConfig = new HashMap<String, String>(SensorsConfig.getConfigMap());
	}

	/**
	 * Updates default config (configurationon jsensors.properties) with a new one
	 * 
	 * @param config
	 *            maps that contains the new config values
	 * @return {@link JSensors} instance
	 */
	public synchronized JSensors config(Map<String, String> config) {
		// Work on a copy: overrides must not modify the defaults for later calls
		if (this.usedConfig == null) {
			this.usedConfig = new HashMap<String, String>(this.baseConfig);
		}

		if (config != null) {
			for (final Map.Entry<String, String> entry : config.entrySet()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("Overriding config entry %s, %s by %s", entry.getKey(),
							this.usedConfig.get(entry.getKey()), entry.getValue()));
				}
				this.usedConfig.put(entry.getKey(), entry.getValue());
			}
		}

		return this;
	}

	/**
	 * Retrieve all sensors components values. The supported sensors types are:
	 * <ul>
	 * <li>Fan: fan speed</li>
	 * <li>Load: component load %</li>
	 * <li>Temperature: temperature of sensor in C(Centigrader) or F(Farenheit)
	 * depending on system settings</li>
	 * </ul>
	 * <p>
	 * 
	 * @return {@link Components} object that containt the lists of components
	 */
	public Components components() {
		Map<String, String> config;
		synchronized (this) {
			config = (this.usedConfig != null) ? this.usedConfig : new HashMap<String, String>(this.baseConfig);
			// Reset config so the next call starts from the defaults again
			this.usedConfig = null;
		}

		return SensorsLocator.get.getComponents(config);
	}

	/**
	 * Releases the resources held by the underlying managers.
	 * <p>
	 * On Windows a PowerShell session is kept open to query the sensors, and that
	 * process prevents the JVM from terminating on its own. Call this method when the
	 * application is shutting down.
	 */
	public void close() {
		if (OSDetector.isWindows()) {
			PowerShellOperations.GET.close();
		}
	}

	/**
	 * Standalone entry point
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		boolean guiMode = false;
		Map<String, String> overriddenConfig = new HashMap<String, String>();
		for (final String arg : args) {
			if ("--debug".equals(arg)) {
				overriddenConfig.put("debugMode", "true");
			}
			if ("--gui".equals(arg)) {
				guiMode = true;
			}
		}

		if (guiMode) {
			GuiOutput.showOutput(overriddenConfig);
		} else {
			ConsoleOutput.showOutput(overriddenConfig);
		}
	}
}
