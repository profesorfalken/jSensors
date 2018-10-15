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

import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
public class SensorsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

	private static File tempFile = null;

	// Hides constructor
	private SensorsUtils() {

	}

	public static String generateLibTmpPath(String libName) {
		return generateLibTmpPath("/", libName);
	}

	public static String generateLibTmpPath(String path, String libName) {
	    if (tempFile == null) {
            InputStream in = SensorsUtils.class.getResourceAsStream(path + libName);
            try {
                tempFile = File.createTempFile(libName, "");
                tempFile.deleteOnExit();
                byte[] buffer = new byte[1024];
                int read;
                FileOutputStream fos = new FileOutputStream(tempFile);
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.close();
                in.close();
            } catch (IOException ex) {
                LOGGER.error("Cannot generate temporary file", ex);
                return "";
            }
        }

		return tempFile.getAbsolutePath();
	}
}
