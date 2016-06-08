/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.manager.unix;

/**
 *
 * @author javier
 */
public interface CFeatureType {
    static int SENSORS_FEATURE_IN = 0x00;
    static int SENSORS_FEATURE_FAN = 0x01;
    static int SENSORS_FEATURE_TEMP = 0x02;
    static int SENSORS_FEATURE_POWER = 0x03;
    static int SENSORS_FEATURE_ENERGY = 0x04;
    static int SENSORS_FEATURE_CURR = 0x05;
    static int SENSORS_FEATURE_HUMIDITY = 0x06;
    static int SENSORS_FEATURE_MAX_MAIN = SENSORS_FEATURE_HUMIDITY + 1;
    static int SENSORS_FEATURE_VID = 0x10;
    static int SENSORS_FEATURE_INTRUSION = 0x11;
    static int SENSORS_FEATURE_MAX_OTHER = SENSORS_FEATURE_INTRUSION + 1;
    static int SENSORS_FEATURE_BEEP_ENABLE = 0x18;
    static int SENSORS_FEATURE_MAX = Integer.MAX_VALUE;
    static int SENSORS_FEATURE_UNKNOWN = Integer.MAX_VALUE;
}
