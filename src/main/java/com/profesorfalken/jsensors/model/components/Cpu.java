/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.model.components;

import com.profesorfalken.jsensors.model.sensors.Sensors;

/**
 *
 * @author javier
 */
public class Cpu {
    public final String name;
    public final Sensors sensors;

    public Cpu(String name, Sensors sensors) {
        this.name = name;
        this.sensors = sensors;
    }
}
