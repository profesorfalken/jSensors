![](https://img.shields.io/maven-central/v/com.profesorfalken/jSensors.svg)
![](https://img.shields.io/github/license/profesorfalken/jSensors.svg)

# jSensors
Monitorize all the hardware sensors of your PC using Java

## Installation ##

To install jSensors you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/com.profesorfalken/jSensors/1.0-PreAlpha

For example, for Maven you have just to add to your pom.xml: 

     <dependency>
          <groupId>com.profesorfalken</groupId>
          <artifactId>jSensors</artifactId>
          <version>1.0-PreAlpha</version>
     </dependency>


Instead, you can direct download the standalone JAR file (that includes its dependencies) and add it to your classpath. 
[TODO]

## Basic Usage ##

### Use as a library ###

In order to retrieve sensors data, it is only necessary to call the method _components()_.

It will retrieve a list of hardware components: CPUs, GPUs, Disks...

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

## Configuring jSensors ##

In order to change jSensors configuration you can either:

#### Override config file for your project ####

You only have to create in your classpaht a file with the name _jsensors.properties_.

For the moment (v1.0-PreAlfa) the only modificable parameters (and its default values) are: 

    # Used for unit testing 
    testMode=REAL
    stubFile=stubFileName

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
