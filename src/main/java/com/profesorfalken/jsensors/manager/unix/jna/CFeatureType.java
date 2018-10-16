/*
 * Copyright 2016-2018 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.manager.unix.jna;

/**
 *
 * @author Javier Garcia Alonso
 */
public interface CFeatureType {
	int SENSORS_FEATURE_IN = 0x00;
	int SENSORS_FEATURE_FAN = 0x01;
	int SENSORS_FEATURE_TEMP = 0x02;
	int SENSORS_FEATURE_POWER = 0x03;
	int SENSORS_FEATURE_ENERGY = 0x04;
	int SENSORS_FEATURE_CURR = 0x05;
	int SENSORS_FEATURE_HUMIDITY = 0x06;
	int SENSORS_FEATURE_MAX_MAIN = SENSORS_FEATURE_HUMIDITY + 1;
	int SENSORS_FEATURE_VID = 0x10;
	int SENSORS_FEATURE_INTRUSION = 0x11;
	int SENSORS_FEATURE_MAX_OTHER = SENSORS_FEATURE_INTRUSION + 1;
	int SENSORS_FEATURE_BEEP_ENABLE = 0x18;
	int SENSORS_FEATURE_MAX = Integer.MAX_VALUE;
	int SENSORS_FEATURE_UNKNOWN = Integer.MAX_VALUE;
}
