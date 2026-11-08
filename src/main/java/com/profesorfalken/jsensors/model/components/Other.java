/*
 * Copyright 2016-2018 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.model.components;

import com.profesorfalken.jsensors.model.sensors.Sensors;

/**
 * A component whose type is not CPU, GPU, disk or motherboard: batteries, network cards,
 * memory modules, USB-C power supplies and any other chip that exposes sensors but does
 * not fit those four categories.
 * <p>
 * Its purpose is not to lose information: before, those components were either dropped or
 * filed under an invented category.
 *
 * @author Javier Garcia Alonso
 */
public class Other extends Component {

	public Other(String name, Sensors sensors) {
		super(name, sensors);
	}

}
