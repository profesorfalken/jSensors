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
package com.profesorfalken.jsensors.manager.windows.powershell;

import com.profesorfalken.jsensors.util.SensorsUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
class PowerShellScriptHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

	private static final String LINE_BREAK = "\r\n";

	private static File tmpFile = null;

	// Hides constructor
	private PowerShellScriptHelper() {
	}

	private static String dllImport() {
		return "[System.Reflection.Assembly]::LoadFile(\""
				+ SensorsUtils.generateLibTmpPath("/lib/win/", "OpenHardwareMonitorLib.dll") + "\")" + " | Out-Null"
				+ LINE_BREAK;
	}

	private static String newComputerInstance() {
		StringBuilder code = new StringBuilder();

		code.append("$PC = New-Object OpenHardwareMonitor.Hardware.Computer").append(LINE_BREAK);

		code.append("$PC.MainboardEnabled = $true").append(LINE_BREAK);
		code.append("$PC.CPUEnabled = $true").append(LINE_BREAK);
		code.append("$PC.RAMEnabled = $true").append(LINE_BREAK);
		code.append("$PC.GPUEnabled = $true").append(LINE_BREAK);
		code.append("$PC.FanControllerEnabled = $true").append(LINE_BREAK);
		code.append("$PC.HDDEnabled = $true").append(LINE_BREAK);

		return code.toString();
	}

	private static String sensorsQueryLoop() {
		StringBuilder code = new StringBuilder();

		code.append("try").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$PC.Open()").append(LINE_BREAK);		
		code.append("}").append(LINE_BREAK);
		code.append("catch").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$PC.Open()").append(LINE_BREAK);
		code.append("}").append(LINE_BREAK);

		code.append("ForEach ($hw in $PC.Hardware)").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$hw").append(LINE_BREAK);
		code.append("$hw.Update()").append(LINE_BREAK);
		code.append("ForEach ($subhw in $hw.SubHardware)").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$subhw.Update()").append(LINE_BREAK);
		code.append("ForEach ($sensor in $subhw.Sensors)").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$sensor").append(LINE_BREAK);
		code.append("Write-Host \"\"").append(LINE_BREAK);
		code.append("}").append(LINE_BREAK);
		code.append("}").append(LINE_BREAK);
		code.append("ForEach ($sensor in $hw.Sensors)").append(LINE_BREAK);
		code.append("{").append(LINE_BREAK);
		code.append("$sensor").append(LINE_BREAK);
		code.append("Write-Host \"\"").append(LINE_BREAK);
		code.append("}").append(LINE_BREAK);
		code.append("}");

		return code.toString();
	}

	static String generateScript() {
		FileWriter writer = null;
		String scriptPath = null;

		if (tmpFile == null) {
			try {
				tmpFile = File.createTempFile("jsensors_" + new Date().getTime(), ".ps1");
				tmpFile.deleteOnExit();
				writer = new FileWriter(tmpFile);
				writer.write(getPowerShellScript());
				writer.flush();
				writer.close();
			} catch (Exception ex) {
				LOGGER.error("Cannot create PowerShell script file", ex);
				return "Error";
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException ioe) {
					LOGGER.warn("Error when finish writing Powershell script file", ioe);
				}
			}
		}

		return tmpFile.getAbsolutePath();
	}

	private static String getPowerShellScript() {
		StringBuilder script = new StringBuilder();

		script.append(dllImport());
		script.append(newComputerInstance());
		script.append(sensorsQueryLoop());

		return script.toString();
	}
}
