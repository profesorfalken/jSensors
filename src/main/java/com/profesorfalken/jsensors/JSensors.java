/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.model.Components;
import java.util.List;
import java.util.Map;

/**
 *
 * @author javier
 */
public enum JSensors {
    get;
    
    Map<String, String> baseConfig = null;
    Map<String, String> usedConfig = null;
    
    private JSensors(){
        //Load config from file
    }
    
    public JSensors config(Map<String, String> config) {                
        //Override config
        
        return this;
    }
    
    public Components components() {
        Components components = SensorsLocator.get.getComponents();
        
        //Reset config
        this.usedConfig = this.baseConfig;
        
        return components;
    }
    
    Components components(SensorsManager manager) {
        return SensorsLocator.get.getComponents(manager);
    }
    
    public List<String> warnings() {
        return null;
    }
}