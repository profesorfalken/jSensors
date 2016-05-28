/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.Components;
import java.util.List;
import java.util.Map;

/**
 *
 * @author javier
 */
public enum JSensors {
    get;
    
    private Components hardwareComponents;
    
    private JSensors(){
        
    }
    
    public JSensors config(Map<String, String> config) {
        return this;
    }
    
    public JSensors noRefresh() {
        return this;
    }
    
    public Components components() {
        return this.hardwareComponents;
    }
    
    public JSensors free() {
        return this;
    }
    
    public List<String> warnings() {
        return null;
    }
}