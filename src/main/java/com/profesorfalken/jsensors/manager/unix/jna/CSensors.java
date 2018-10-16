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

import com.sun.jna.Library;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import java.io.File;

/**
 *
 * @author Javier Garcia Alonso
 */
public interface CSensors extends Library {	

	int sensors_init(File input);
	
	void sensors_cleanup();

	CChip sensors_get_detected_chips(CChip[] match, IntByReference nr);

	CFeature sensors_get_features(CChip name, IntByReference nr);

	String sensors_get_label(CChip name, CFeature feature);

	int sensors_get_value(CChip name, int subfeat_nr, DoubleByReference value);

	String sensors_get_adapter_name(CBus bus);

	CSubFeature sensors_get_all_subfeatures(CChip name, CFeature feature, IntByReference nr);
}
