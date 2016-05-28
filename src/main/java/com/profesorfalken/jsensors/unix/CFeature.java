/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.unix;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author javier
 */
class CFeature extends Structure {

    public String name;
    public int number;
    public int type;
    public int first_subfeature;
    public int padding;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("name", "number", "type", "first_subfeature", "padding");
    }

}
