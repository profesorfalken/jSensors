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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that reads the content of the configuration file and convert it
 * into Properties.
 *
 * @author Javier Garcia Alonso
 */
final class SensorsConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorsConfig.class);

	private static final String CONFIG_FILENAME = "jsensors.properties";

	private static Properties config;

	// Hide constructor
	private SensorsConfig() {
	}

	private static Properties getConfig() {
		if (config == null) {
			config = new Properties();
			try {
				// load a properties file from class path, inside static method
				config.load(SensorsConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME));
			} catch (IOException ex) {
				LOGGER.error("Cannot load config file " + CONFIG_FILENAME, ex);
			}
		}

		return config;
	}

	static Map<String, String> getConfigMap() {
		Map<String, String> returnMap = new HashMap<String, String>();
		Properties configProps = getConfig();

		for (final String propertyName : configProps.stringPropertyNames()) {
			returnMap.put(propertyName, configProps.getProperty(propertyName));
		}

		return returnMap;
	}
}
