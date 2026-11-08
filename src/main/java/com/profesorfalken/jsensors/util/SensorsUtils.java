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
package com.profesorfalken.jsensors.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
public final class SensorsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SensorsUtils.class);

	/**
	 * Extracted libraries, keyed by their resource path. A single static field used to be
	 * shared by every library, so asking for a second one returned the first one again.
	 */
	private static final Map<String, File> extractedLibraries = new HashMap<String, File>();

	// Hides constructor
	private SensorsUtils() {

	}

	public static String generateLibTmpPath(String libName) {
		return generateLibTmpPath("/", libName);
	}

	public static synchronized String generateLibTmpPath(String path, String libName) {
		String resource = path + libName;

		File cached = extractedLibraries.get(resource);
		if (cached != null && cached.exists()) {
			return cached.getAbsolutePath();
		}

		InputStream in = SensorsUtils.class.getResourceAsStream(resource);
		if (in == null) {
			// Missing resource: report it instead of failing later with a NullPointerException
			LOGGER.error("Cannot find embedded library {}", resource);
			return "";
		}

		File tempFile = null;
		FileOutputStream fos = null;
		try {
			tempFile = File.createTempFile(libName, "");
			tempFile.deleteOnExit();

			byte[] buffer = new byte[1024];
			int read;
			fos = new FileOutputStream(tempFile);
			while ((read = in.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}

			extractedLibraries.put(resource, tempFile);
			return tempFile.getAbsolutePath();
		} catch (IOException ex) {
			LOGGER.error("Cannot generate temporary file for " + resource, ex);
			return "";
		} finally {
			closeQuietly(fos);
			closeQuietly(in);
		}
	}

	private static void closeQuietly(java.io.Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ex) {
				LOGGER.warn("Cannot close stream", ex);
			}
		}
	}
}
