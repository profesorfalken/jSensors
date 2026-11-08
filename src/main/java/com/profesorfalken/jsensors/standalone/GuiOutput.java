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
package com.profesorfalken.jsensors.standalone;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import com.profesorfalken.jsensors.model.components.Component;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.profesorfalken.jsensors.util.OSDetector;

/**
 * Provides an output using a Swing based GUI
 * 
 * @author Javier Garcia Alonso
 *
 */
public class GuiOutput {

	/**
	 * Sensors are refreshed periodically, on the event dispatch thread. The previous
	 * implementation refreshed them in a loop without any pause, from a background
	 * thread: the window kept a CPU core at 100% and wrote to the table off the EDT.
	 */
	private static final int REFRESH_MILLISECONDS = 2000;

	public static void showOutput(final Map<String, String> config) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JSensorsGUI gui = new GuiOutput().new JSensorsGUI(config);
				gui.setVisible(true);
			}
		});
	}

	@SuppressWarnings("serial")
	class JSensorsGUI extends JFrame {
		private final Map<String, String> config;
		private final JTable table = new JTable();

		public JSensorsGUI(Map<String, String> config) {
			this.config = config;
			initUI();
		}

		private void initUI() {
			setTitle("JSensors");
			setSize(600, 400);
			setLocationRelativeTo(null);

			table.setFillsViewportHeight(true);
			add(new JScrollPane(table));

			if (OSDetector.isWindows() && !PowerShellOperations.isAdministrator()) {
				JOptionPane.showMessageDialog(this,
						"You have not executed jSensors in Administrator mode, so CPU temperature sensors will not be detected.");
			}

			// Closing the window has to release the PowerShell session, otherwise the
			// process keeps the JVM alive
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					JSensors.get.close();
					dispose();
					System.exit(0);
				}
			});

			refresh();
			new Timer(REFRESH_MILLISECONDS, event -> refresh()).start();
		}

		private void refresh() {
			table.setModel(buildModel(JSensors.get.config(this.config).components()));
		}
	}

	/**
	 * Builds the table model for a set of components. It is a static method, so it can be
	 * tested without opening a window.
	 *
	 * @param components components to display
	 * @return table model with one row per component and one row per sensor
	 */
	static DefaultTableModel buildModel(Components components) {
		DefaultTableModel model = new DefaultTableModel(new Object[] { "Sensor Name", "Value" }, 0);

		addComponents(components.cpus, "CPU", model);
		addComponents(components.gpus, "GPU", model);
		addComponents(components.disks, "Disk", model);
		addComponents(components.mobos, "Motherboard", model);
		addComponents(components.others, "Other", model);

		return model;
	}

	private static void addComponents(List<? extends Component> list, String label, DefaultTableModel model) {
		if (list == null) {
			return;
		}
		for (final Component component : list) {
			model.addRow(new String[] { label + " component: " + component.name });
			addSensors(component, model);
		}
	}

	private static void addSensors(Component component, DefaultTableModel model) {
		if (component.sensors == null) {
			return;
		}

		List<Temperature> temperatures = component.sensors.temperatures;
		for (final Temperature temperature : temperatures) {
			model.addRow(new String[] { temperature.name + ": ", temperature.value + " C" });
		}

		List<Fan> fans = component.sensors.fans;
		for (final Fan fan : fans) {
			model.addRow(new String[] { fan.name + ": ", fan.value + " RPM" });
		}

		List<Load> loads = component.sensors.loads;
		for (final Load load : loads) {
			model.addRow(new String[] { load.name + ": ", load.value + "" });
		}
	}
}
