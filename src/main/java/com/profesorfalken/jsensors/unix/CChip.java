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
public class CChip extends Structure {

    public String prefix;
    public CBus bus;
    public String path;
    public int addr;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("prefix", "bus", "path", "addr");
    }

}
