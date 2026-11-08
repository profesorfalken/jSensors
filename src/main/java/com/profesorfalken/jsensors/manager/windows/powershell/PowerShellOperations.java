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
import com.profesorfalken.jpowershell.PowerShellResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
public enum PowerShellOperations {
	GET;

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

	private static final String IS_ADMIN_COMMAND = "([Security.Principal.WindowsPrincipal] "
			+ "[Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole("
			+ "[Security.Principal.WindowsBuiltInRole] \"Administrator\")";

	private PowerShell powerShell = null;

	/**
	 * Returns the PowerShell session used to query the sensors, creating it on first use.
	 * <p>
	 * The session used to be created in the enum constructor, so merely loading this class
	 * started a PowerShell process.
	 *
	 * @return open session
	 */
	private synchronized PowerShell session() {
		if (this.powerShell == null) {
			this.powerShell = PowerShell.openSession();
		}
		return this.powerShell;
	}

	public static boolean isAdministrator() {
		try {
			PowerShellResponse response = PowerShell.executeSingleCommand(IS_ADMIN_COMMAND);
			// executeSingleCommand returns null when PowerShell is not available
			return response != null && "true".equalsIgnoreCase(response.getCommandOutput().trim());
		} catch (RuntimeException ex) {
			LOGGER.warn("Cannot check if the process is running as administrator", ex);
			return false;
		}
	}

	public String getRawSensorsData() {
		return session().executeScript(PowerShellScriptHelper.generateScript()).getCommandOutput();
	}

	/**
	 * Closes the session and the PowerShell process behind it.
	 */
	public synchronized void close() {
		if (this.powerShell != null) {
			try {
				this.powerShell.close();
			} catch (RuntimeException ex) {
				LOGGER.warn("Cannot close the PowerShell session", ex);
			} finally {
				this.powerShell = null;
			}
		}
	}
}
