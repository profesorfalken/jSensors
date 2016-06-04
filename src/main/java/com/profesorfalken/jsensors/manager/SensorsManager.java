/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Sensors;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author javier
 */
public abstract class SensorsManager {

    protected abstract String getSensorsData();

    public Components getComponents() {
        Cpu cpu = null;
        Gpu gpu = null;
        Disk disk = null;

        String rawSensorsData = getSensorsData();

        String[] componentsData = rawSensorsData.split("\\[COMPONENT\\]\n");

        for (final String componentData : componentsData) {
            if (componentData.startsWith("CPU")) {
                cpu = getCpu(componentData);
            } else if (componentData.startsWith("GPU")) {
                gpu = getGpu(componentData);
            } else if (componentData.startsWith("DISK")) {
                disk = getDisk(componentData);
            }
        }

        return new Components(cpu, gpu, disk);
    }

    private Cpu getCpu(String cpuData) {
        return new Cpu(getName(cpuData), getSensors(cpuData));
    }
    
     private Gpu getGpu(String gpuData) {
        return new Gpu(getName(gpuData), getSensors(gpuData));
    }

    private Disk getDisk(String diskData) {
        return new Disk(getName(diskData), getSensors(diskData));
    }
    
    private String getName(String componentData) {
        String name = null;
        
        String[] dataLines = componentData.split("\\r?\\n");
        for (final String dataLine : dataLines) {
            if (dataLine.startsWith("Label")) {
                name = dataLine.split(":")[1].trim();
                break;
            }
        }
        
        return name;
    }
    
    private Sensors getSensors(String componentData) {
        List<Temperature> temperatures = new ArrayList<Temperature>();
        List<Fan> fans = new ArrayList<Fan>();

        String[] dataLines = componentData.split("\\r?\\n");
        
        for (final String dataLine : dataLines) {
            if (dataLine.startsWith("Temp")) {
                String[] data = dataLine.split(":");
                Temperature temperature = new Temperature(data[0].trim(),
                        Double.valueOf(data[1].trim()));
                temperatures.add(temperature);
            } else if (dataLine.startsWith("Fan")) {
                String[] data = dataLine.split(":");
                Fan fan = new Fan(data[0].trim(),
                        Double.valueOf(data[1].trim()));
                fans.add(fan);
            }
        }
        
        return new Sensors(temperatures, fans);
    }   
}
