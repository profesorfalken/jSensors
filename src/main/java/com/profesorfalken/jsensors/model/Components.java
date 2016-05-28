/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.jsensors.model;

/**
 *
 * @author javier
 */
public class Components {
    public final Cpu cpu;
    public final Gpu gpu;
    public final Disk disk;

    public Components(Cpu cpu, Gpu gpu, Disk disk) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.disk = disk;
    }    
}
