/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.components.Components;
import java.util.List;
import java.util.Map;

/**
 *
 * @author javier
 */
public enum JSensors {
    get;
    
    final Map<String, String> baseConfig;
    
    Map<String, String> usedConfig = null;
    
    private JSensors(){
        //Load config from file
        baseConfig = SensorsConfig.getConfigMap();
    }
    
    public JSensors config(Map<String, String> config) {                
        //Override config
        this.usedConfig = this.baseConfig;        
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            this.usedConfig.put(entry.getKey(), entry.getValue());
        }
        
        return this;
    }
    
    public Components components() {
        Components components = SensorsLocator.get.getComponents(this.usedConfig);
        
        //Reset config
        this.usedConfig = this.baseConfig;
        
        return components;
    }
    
    public List<String> warnings() {
        return null;
    }
}