/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.model.sensors;

/**
 *
 * @author javier
 */
public class Fan {
    public final String name;
    public final Double value;

    public Fan(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}
