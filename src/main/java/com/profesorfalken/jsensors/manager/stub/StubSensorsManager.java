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
    private final String stubContent;
    
    public StubSensorsManager(String stubContent) {
        this.stubContent = stubContent;
    }

    public String getSensorsData() {
        return this.stubContent;
    }
}
