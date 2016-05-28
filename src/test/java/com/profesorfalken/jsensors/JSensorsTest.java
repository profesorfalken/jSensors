/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.Cpu;
import com.profesorfalken.jsensors.model.Fan;
import com.profesorfalken.jsensors.model.Temperature;
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

    /**
     * Test Cpu information
     */
    @Test
    public void testCpu() {
        System.out.println("Testing CPU sensors");
        
        //Get CPU component
        Cpu cpu = JSensors.get.components().cpu;
        
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
            assertTrue("Fan RPM value should be lower than 120, but was "
                    + fan.value, fan.value < 120);
            System.out.println("Fan RPM: " + fan.value);            
        }
    }    
}