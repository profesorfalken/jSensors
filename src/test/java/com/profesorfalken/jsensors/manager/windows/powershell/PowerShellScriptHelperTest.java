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
package com.profesorfalken.jsensors.manager.windows.powershell;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 * The generated script is the only place where the OpenHardwareMonitor API is used, so it
 * is worth checking that it keeps the safety nets.
 *
 * @author Javier Garcia Alonso
 */
public class PowerShellScriptHelperTest {

	@Test
	public void testScriptLoadsTheLibraryAndEnablesPropertiesSafely() throws Exception {
		String scriptPath = PowerShellScriptHelper.generateScript();
		String script = new String(Files.readAllBytes(Paths.get(new File(scriptPath).toURI())),
				StandardCharsets.UTF_8);

		Assert.assertTrue("The library is not loaded", script.contains("LoadFile("));
		Assert.assertTrue("The computer instance is not created",
				script.contains("New-Object OpenHardwareMonitor.Hardware.Computer"));

		// A renamed property must not abort the whole script
		Assert.assertTrue("Properties are not checked before being set",
				script.contains("PSObject.Properties"));

		// Without this line there is no way to know which library produced the data
		Assert.assertTrue("The library version is not reported",
				script.contains("OpenHardwareMonitor-Version"));
	}
}
