/*
 * Copyright 2016 Javier Garcia Alonso.
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
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public class JSensorsTest {
    Logger logger = LoggerFactory.getLogger(JSensorsTest.class);
    
    private static final String TESTSET_1 = "testset_1.jsensor";
    
    public JSensorsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
    }
    
    @After
    public void tearDown() {
    }
    
    private JSensors getJSensorsStub(String testset) throws IOException {
        Map<String, String> config = new HashMap<String, String>();
        
        config.put("testMode", "STUB");
        
        InputStream is = JSensorsTest.class.getClassLoader().getResourceAsStream(testset);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            config.put("stubContent", sb.toString());
        } finally {
            br.close();
        }        
        
        return JSensors.get.config(config);
    }

    /**
     * Test Cpu information
     * @throws java.lang.Exception
     */
    @Test
    public void testCpu() throws Exception{
        logger.info("Testing CPU sensors");
        
        //Get CPU component
        List<Cpu> cpus = getJSensorsStub(TESTSET_1).components().cpus;
        
        assertNotNull("Cannot recover CPU data", cpus);
        assertTrue("No CPUs found", cpus.size() > 0);
        
        Cpu cpu = cpus.get(0);
        assertNotNull("Cannot recover CPU data", cpu);
        
        assertNotNull("No CPU name", cpu.name);
        logger.info("CPU name: " + cpu.name);
        
        //Test temperature sensors (in C)
        for (final Temperature temp : cpu.sensors.temperatures) {
            assertNotNull("Temperature should not be null", temp);
            assertNotNull("Temperature value should not be null", temp.value);
            assertTrue("Temperature value should be greater than 0, but was "
                    + temp.value, temp.value > 0);
            assertTrue("Temperature value should be lower than 120, but was "
                    + temp.value, temp.value < 120);
            logger.info("Temperature: " + temp.value);            
        }
        
        //Test Fan speed sensors (in RPM)
        for (final Fan fan : cpu.sensors.fans) {
            assertNotNull("Fan should not be null", fan);
            assertNotNull("Fan RPM should not be null", fan.value);
            assertTrue("Fan RPM should be greater than 0, but was "
                    + fan.value, fan.value > 0);
            assertTrue("Fan RPM value should be lower than 5000, but was "
                    + fan.value, fan.value < 5000);
            logger.info("Fan RPM: " + fan.value);            
        }
    }    
    
    /**
     * Test Gpu information
     * @throws java.lang.Exception
     */
    @Test
    public void testGpu() throws Exception{
        logger.info("Testing GPU sensors");
        
        //Get GPU component
        //Get CPU component
        List<Gpu> gpus = getJSensorsStub(TESTSET_1).components().gpus;
        
        assertNotNull("Cannot recover GPU data", gpus);
        assertTrue("No GPUs found", gpus.size() > 0);
        
        Gpu gpu = gpus.get(0);
        assertNotNull("Cannot recover GPU data", gpu);
        
        assertNotNull("No GPU name", gpu.name);
        logger.info("GPU name: " + gpu.name);
        
        //Test temperature sensors (in C)
        for (final Temperature temp : gpu.sensors.temperatures) {
            assertNotNull("Temperature should not be null", temp);
            assertNotNull("Temperature value should not be null", temp.value);
            assertTrue("Temperature value should be greater than 0, but was "
                    + temp.value, temp.value > 0);
            assertTrue("Temperature value should be lower than 120, but was "
                    + temp.value, temp.value < 120);
            logger.info("Temperature: " + temp.value);            
        }
        
        //Test Fan speed sensors (in RPM)
        for (final Fan fan : gpu.sensors.fans) {
            assertNotNull("Fan should not be null", fan);
            assertNotNull("Fan RPM should not be null", fan.value);
            assertTrue("Fan RPM should be greater than 0, but was "
                    + fan.value, fan.value > 0);
            assertTrue("Fan RPM value should be lower than 5000, but was "
                    + fan.value, fan.value < 5000);
            logger.info("Fan RPM: " + fan.value);            
        }
    }    
    
    /**
     * Test Disk information
     * @throws java.lang.Exception
     */
    @Test
    public void testDisk() throws Exception{
        logger.info("Testing CPU sensors");
        
        //Get Disk component
        //Get CPU component
        List<Disk> disks = getJSensorsStub(TESTSET_1).components().disks;
        
        assertNotNull("Cannot recover Disk data", disks);
        assertTrue("No Disks found", disks.size() > 0);
        
        Disk disk = disks.get(0);
        assertNotNull("Cannot recover Disk data", disk);
        
        assertNotNull("No Disk name", disk.name);
        logger.info("Disk name: " + disk.name);
        
        //Test temperature sensors (in C)
        for (final Temperature temp : disk.sensors.temperatures) {
            assertNotNull("Temperature should not be null", temp);
            assertNotNull("Temperature value should not be null", temp.value);
            assertTrue("Temperature value should be greater than 0, but was "
                    + temp.value, temp.value > 0);
            assertTrue("Temperature value should be lower than 120, but was "
                    + temp.value, temp.value < 120);
            logger.info("Temperature: " + temp.value);            
        }
        
        //Test Fan speed sensors (in RPM)
        for (final Fan fan : disk.sensors.fans) {
            assertNotNull("Fan should not be null", fan);
            assertNotNull("Fan RPM should not be null", fan.value);
            assertTrue("Fan RPM should be greater than 0, but was "
                    + fan.value, fan.value > 0);
            assertTrue("Fan RPM value should be lower than 5000, but was "
                    + fan.value, fan.value < 5000);
            logger.info("Fan RPM: " + fan.value);            
        }
    }    
}