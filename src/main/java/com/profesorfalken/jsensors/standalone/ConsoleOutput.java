package com.profesorfalken.jsensors.standalone;

import java.util.List;
import java.util.Map;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Component;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

public class ConsoleOutput {
	public static void showOutput(Map<String, String> config) {
		System.out.println("Scanning sensors data...");

		Components components = JSensors.get.config(config).components();

		List<Cpu> cpus = components.cpus;
		if (cpus != null) {
			for (final Cpu cpu : cpus) {
				System.out.println("Found CPU component: " + cpu.name);
				readComponent(cpu);
			}
		}

		List<Gpu> gpus = components.gpus;
		if (gpus != null) {
			for (final Gpu gpu : gpus) {
				System.out.println("Found GPU component: " + gpu.name);
				readComponent(gpu);
			}
		}

		List<Disk> disks = components.disks;
		if (disks != null) {
			for (final Disk disk : disks) {
				System.out.println("Found disk component: " + disk.name);
				readComponent(disk);
			}
		}
	}

	// Read component values in standalone mode
	private static void readComponent(Component component) {
		if (component.sensors != null) {
			System.out.println("Sensors: ");

			List<Temperature> temps = component.sensors.temperatures;
			for (final Temperature temp : temps) {
				System.out.println(temp.name + ": " + temp.value + " C");
			}

			List<Fan> fans = component.sensors.fans;
			for (final Fan fan : fans) {
				System.out.println(fan.name + ": " + fan.value + " RPM");
			}

			List<Load> loads = component.sensors.loads;
			for (final Load load : loads) {
				System.out.println(load.name + ": " + load.value);
			}
		}
	}
}
