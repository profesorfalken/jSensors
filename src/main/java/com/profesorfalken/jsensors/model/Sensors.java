/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.model;

import java.util.List;

/**
 *
 * @author javier
 */
public class Sensors {
    public final List<Temperature> temperatures;
    public final List<Fan> fans;

    public Sensors(List<Temperature> temperatures, List<Fan> fans) {
        this.temperatures = temperatures;
        this.fans = fans;
    }    
}
