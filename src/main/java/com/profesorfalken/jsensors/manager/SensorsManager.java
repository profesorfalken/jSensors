/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager;

import com.profesorfalken.jsensors.model.Components;

/**
 *
 * @author javier
 */
public abstract class SensorsManager {    
    
    protected abstract String getSensorsData();
    
    public Components getComponents() {
        return null;
    }
}
