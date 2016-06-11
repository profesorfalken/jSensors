/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.components.Components;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public enum JSensors {

    get;

    private static final Logger LOGGER = LoggerFactory.getLogger(JSensors.class);

    final Map<String, String> baseConfig;

    Map<String, String> usedConfig = null;

    private JSensors() {
        //Load config from file
        baseConfig = SensorsConfig.getConfigMap();
    }

    public JSensors config(Map<String, String> config) {
        //Override config
        this.usedConfig = this.baseConfig;
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Overriding config entry %s, %s by %s", 
                        entry.getKey(), this.usedConfig.get(entry.getKey()), entry.getValue()));
            }
            this.usedConfig.put(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public Components components() {
        if (this.usedConfig == null) {
            this.usedConfig = new HashMap<String, String>();
        }
        
        Components components = SensorsLocator.get.getComponents(this.usedConfig);

        //Reset config
        this.usedConfig = this.baseConfig;

        return components;
    }
    
    public static void main(String[] args) {        
        Map<String, String> overriddenConfig = new HashMap<String, String>();
        for (final String arg : args) {
            if ("--debug".equals(arg)) {
                overriddenConfig.put("debugMode", "true");
            }
        }
        JSensors.get.config(overriddenConfig).components();
    }
}
