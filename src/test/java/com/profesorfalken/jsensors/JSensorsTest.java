/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author javier
 */
public class JSensorsTest {
    
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
     */
    @Test
    public void testCpu() throws Exception{
        System.out.println("Testing CPU sensors");
        
        //Get CPU component
        Cpu cpu = getJSensorsStub(TESTSET_1).components().cpu;
        
        assertNotNull("Cannot recover CPU data", cpu);
        
        assertNotNull("No CPU name", cpu.name);
        
        //Test temperature sensors (in C)
        for (final Temperature temp : cpu.sensors.temperatures) {
            assertNotNull("Temperature should not be null", temp);
            assertNotNull("Temperature value should not be null", temp.value);
            assertTrue("Temperature value should be greater than 0, but was "
                    + temp.value, temp.value > 0);
            assertTrue("Temperature value should be lower than 120, but was "
                    + temp.value, temp.value < 120);
            System.out.println("Temperature: " + temp.value);            
        }
        
        //Test Fan speed sensors (in RPM)
        for (final Fan fan : cpu.sensors.fans) {
            assertNotNull("Fan should not be null", fan);
            assertNotNull("Fan RPM should not be null", fan.value);
            assertTrue("Fan RPM should be greater than 0, but was "
                    + fan.value, fan.value > 0);
            assertTrue("Fan RPM value should be lower than 5000, but was "
                    + fan.value, fan.value < 5000);
            System.out.println("Fan RPM: " + fan.value);            
        }
    }    
}