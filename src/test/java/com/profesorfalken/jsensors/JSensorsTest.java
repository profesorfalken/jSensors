/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors;

import java.util.List;
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
     * Test of values method, of class JSensors.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        JSensors[] expResult = null;
        JSensors[] result = JSensors.values();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of valueOf method, of class JSensors.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String name = "";
        JSensors expResult = null;
        JSensors result = JSensors.valueOf(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of refresh method, of class JSensors.
     */
    @Test
    public void testRefresh() {
        System.out.println("refresh");
        JSensors instance = null;
        HardwareSensors expResult = null;
        HardwareSensors result = instance.refresh();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of free method, of class JSensors.
     */
    @Test
    public void testFree() {
        System.out.println("free");
        JSensors instance = null;
        JSensors expResult = null;
        JSensors result = instance.free();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of warnings method, of class JSensors.
     */
    @Test
    public void testWarnings() {
        System.out.println("warnings");
        JSensors instance = null;
        List<String> expResult = null;
        List<String> result = instance.warnings();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
