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

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

import static com.profesorfalken.jsensors.manager.windows.powershell.PowerShellScriptHelper.*;

/**
 *
 * @author Javier Garcia Alonso
 */
public class PowerShellOperations implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

	private PowerShell powerShell = null;

	public PowerShellOperations() {
		this.powerShell = PowerShell.openSession();
		this.powerShell.executeCommand(dllImport());
		this.powerShell.executeCommand(newComputerInstance());
	}

	public static boolean isAdministrator() {
		String command = "([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] \"Administrator\")";

		return "true".equalsIgnoreCase(PowerShell.executeSingleCommand(command).getCommandOutput());
	}

	public String getRawSensorsData() {
		return this.powerShell.executeCommand(sensorsQueryLoop()).getCommandOutput();
	}

	@Override
	public void close() {
		powerShell.close();
	}
}
