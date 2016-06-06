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
public class CBus extends Structure {

    public short type;
    public short nr;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("type", "nr");
    }

}
