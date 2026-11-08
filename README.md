![](https://img.shields.io/maven-central/v/com.profesorfalken/jSensors.svg)
![](https://img.shields.io/github/license/profesorfalken/jSensors.svg)
[![CI](https://github.com/profesorfalken/jSensors/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/profesorfalken/jSensors/actions/workflows/ci.yml)

# jSensors
Monitorize all the hardware sensors of your PC using Java

#### Windows Example Output ####

    Scanning sensors data...
    Found CPU component Intel Core i7-3770
    Sensors: 
    Temp CPU Core #1: 46.0 C
    Temp CPU Core #2: 43.0 C
    Temp CPU Core #3: 45.0 C
    Temp CPU Core #4: 45.0 C
    Temp CPU Package: 46.0 C
    Found GPU component AMD Radeon HD 7470
    Sensors: 
    Temp GPU Core: 53.0 C
    Found disk component ST250DM000-1BD141
    Sensors: 
    Temp Temperature: 34.0 C

#### Linux Example Output ####

    Scanning sensors data...
    Found CPU component: coretemp
    Sensors: 
    Temp Package id 0: 76.0 C
    Temp Core 0: 74.0 C
    Temp Core 1: 62.0 C
    Temp Core 2: 72.0 C
    Temp Core 3: 63.0 C
    Found disk component: nvme
    Sensors: 
    Temp Composite: 31.85 C
    Temp Sensor 1: 31.85 C

Components are named after the sensor chip they come from (`coretemp`, `nvme`, `acpitz`,
`it8728`...), which is what identifies them. Chips that do not map to a supported
component type (batteries, WiFi cards, USB-C power supplies) are not reported.

**IMPORTANT NOTE:** if you are getting 0.0 as temperature value, try to launch as Administrator

## Installation ##

To install jSensors you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/com.profesorfalken/jSensors/3.0.0

For example, for Maven you have just to add to your pom.xml: 

     <dependency>
          <groupId>com.profesorfalken</groupId>
          <artifactId>jSensors</artifactId>
          <version>3.0.0</version>
     </dependency>


## Basic Usage ##

**Requirements:** Java 8 or later.

### Use as a library ###

In order to retrieve sensors data, it is only necessary to call the method _components()_.

It will retrieve a list of hardware components: CPUs, GPUs, Disks and motherboards. Any other chip that exposes sensors (batteries, network cards, USB-C power supplies...) is returned in `components.others`, so no information is lost.

#### Get CPU sensors (temperature and fans) ####
```java
    Components components = JSensors.get.components();

    List<Cpu> cpus = components.cpus;
    if (cpus != null) {
        for (final Cpu cpu : cpus) {
            System.out.println("Found CPU component: " + cpu.name);
            if (cpu.sensors != null) {
              System.out.println("Sensors: ");
  
              //Print temperatures
              List<Temperature> temps = cpu.sensors.temperatures;
              for (final Temperature temp : temps) {
                  System.out.println(temp.name + ": " + temp.value + " C");
              }
  
              //Print fan speed
              List<Fan> fans = cpu.sensors.fans;
              for (final Fan fan : fans) {
                  System.out.println(fan.name + ": " + fan.value + " RPM");
              }
            }
        }
    }
```

Same for other hardware components as GPU or Disks.

On Windows the library keeps a PowerShell process open to read the sensors. Call `JSensors.get.close()` when your application shuts down, otherwise the process will keep the JVM alive.

### Use as a standalone application ###

First of all, download the JAR file containing all dependencies: https://github.com/profesorfalken/profesorfalken.github.io/raw/master/files/jSensors-2.2-jar-with-dependencies.jar

Execute jSensors and get all sensors data: 

```
    java -jar jsensors-3.0.0-jar-with-dependencies.jar
```

This will generate a console output. 


It is also possible to show a simple gui with all the sensors data:

```
    java -jar jsensors-3.0.0-jar-with-dependencies.jar --gui
```

Result:

![jSensorsGUI](https://raw.githubusercontent.com/profesorfalken/profesorfalken.github.io/master/files/jsensors-gui.png)

## Configuring jSensors ##

In order to change jSensors configuration you can either:

#### Override config file for your project ####

You only have to create in your classpaht a file with the name _jsensors.properties_.

For the moment the only modificable parameters (and its default values) are: 
    
    # Test mode
    # REAL: test on real hardware
    # STUB: use simulated/hardcoded results to test
    testMode=REAL
    # Stub Content
    # string value of the simulated results
    stubContent=""
    # Debug mode
    # If activated it logs in console all retrieved details
    debugMode=false
     
#### Override config element for one request ####
  
When performing a request we can easily override config elements: 
  
  ```java
      Map<String, String> overriddenConfig = new HashMap<String, String>();
      overriddenConfig.put("debugMode", "true");
  
      [...]
      Components components = JSensors.get.config(overriddenConfig).components();
  ```
