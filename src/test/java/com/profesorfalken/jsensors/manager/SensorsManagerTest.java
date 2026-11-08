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
package com.profesorfalken.jsensors.manager;

import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

/**
 * Value parsing used to depend on the default locale: in Spanish or German, "80.0" was
 * parsed as 800 and "30.85" as 3085, because the dot is the grouping separator there.
 *
 * @author Javier Garcia Alonso
 */
public class SensorsManagerTest {

	@Test
	public void testValueParsingIgnoresDefaultLocale() {
		Locale previous = Locale.getDefault();
		try {
			Locale.setDefault(new Locale("es", "ES"));

			Assert.assertEquals(80.0, SensorsManager.parseValue("80.0"), 0.0001);
			Assert.assertEquals(30.85, SensorsManager.parseValue("30.85"), 0.0001);
		} finally {
			Locale.setDefault(previous);
		}
	}

	@Test
	public void testValueParsingAcceptsDecimalComma() {
		Assert.assertEquals(1500.5, SensorsManager.parseValue("1500,5"), 0.0001);
	}

	@Test
	public void testValueParsingWithUnits() {
		Assert.assertEquals(33.85, SensorsManager.parseValue("33.85 C"), 0.0001);
	}

	@Test
	public void testValueParsingWithNegativeValue() {
		Assert.assertEquals(-5.0, SensorsManager.parseValue("-5.0"), 0.0001);
	}

	@Test(expected = NumberFormatException.class)
	public void testValueParsingRejectsText() {
		SensorsManager.parseValue("Could not retrieve value");
	}
}
