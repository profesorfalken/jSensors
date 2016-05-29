/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.unix;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author javier
 */
class CSubFeature extends Structure {

    public String name;
    public int number;
    public int type;
    public int mapping;
    public int flags;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("name", "number", "type", "mapping", "flags");
    }

}
