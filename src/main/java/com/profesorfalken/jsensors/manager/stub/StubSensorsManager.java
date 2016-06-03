/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.stub;

import com.profesorfalken.jsensors.manager.SensorsManager;

/**
 *
 * @author javier
 */
public class StubSensorsManager extends SensorsManager {

    public String getSensorsData() {
        StringBuilder sensorsData = new StringBuilder();
        
        sensorsData.append("[COMPONENT]");
        sensorsData.append("CPU");
        sensorsData.append("Label: Pentium G3258 @ 3200Mhz");
        sensorsData.append("Temp Core 1: 25.0");
        sensorsData.append("Temp Core 2: 25.0");
        sensorsData.append("Fan 1: 850");
        
        sensorsData.append("[COMPONENT]");
        sensorsData.append("GPU");
        sensorsData.append("Label: MSI Nvidia 750 Ti");
        sensorsData.append("Temp: 32.5");
        sensorsData.append("Fan 1: 998");
        
        sensorsData.append("[COMPONENT]");
        sensorsData.append("DISK");
        sensorsData.append("Label: Seagate 1GB");
        sensorsData.append("Temp: 31.0");

        return sensorsData.toString();
    }
}
