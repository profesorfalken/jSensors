/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import java.util.List;

/**
 *
 * @author javier
 */
public enum JSensors {
    get;
    
    private HardwareSensors sensorData;
    
    private JSensors(){
        
    }
    
    public HardwareSensors refresh() {
        return this.sensorData;
    }
    
    public JSensors free() {
        return this;
    }
    
    public List<String> warnings() {
        return null;
    }
}
