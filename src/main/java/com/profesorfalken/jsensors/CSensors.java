/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import java.io.File;

/**
 *
 * @author javier
 */
public interface CSensors extends Library {

    public int sensors_init(File input);

    public CChip sensors_get_detected_chips(CChip[] match, IntByReference nr);

    public CFeature sensors_get_features(CChip name, IntByReference nr);

    public String sensors_get_label(CChip name, CFeature feature);

    public int sensors_get_value(CChip name, int subfeat_nr, DoubleByReference value);

    public String sensors_get_adapter_name(CBus bus);

    public CSubFeature sensors_get_all_subfeatures(CChip name,
            CFeature feature, IntByReference nr);
}
