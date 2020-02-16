[![][badge-license]][license]
[![][badge-ci]][workflow-ci]

![openapi-processor-gradle logo](images/openapi-processor-gradle@1280x200.png)


# com.github.hauner.openapi.gradle 

a gradle plugin based on the [openapi-processor-api][oap-api] to handle any openapi-processor without
an explicit dependency on the processor. Requires Gradle 5.2 or better.

# gradle dsl

A processor-spring specific description is available in [`Using Gradle`][oap-spring-gradle] in
 the documentation of [openapi-processor-spring][oap-spring].

The plugin adds a new configuration block `openapiProcessor` to the gradle project. Each processor
is configured by a nested configuration block.
 
Apart from that there is only a single option that is recognized inside the configuration block:

* `apiPath`, which defines the path to the openapi yaml file. This is usually the same for all
processors and placing it directly into the the `openapiProcessor` block sets it for all processors.

To configure a processor, for example the [openapi-processor-spring][oap-spring], a `spring`
configuration is placed into the the `openapiProcessor` block. The name of the configuration is
used to create a gradle task `process<Name>` to run the corresponding processor.


    openapiProcessor {

        // the path to the open api yaml file.
        apiPath "${projectDir}/src/api/openapi.yaml"
        
        spring {
            ... options of openapi-processor-spring
        }
        
    }
    
    
In case the json processor is needed it is added in the same way:


    openapiProcessor {

        // the path to the open api yaml file.
        apiPath "${projectDir}/src/api/openapi.yaml"
        
        spring {
            ... options of openapi-processor-spring 
        }

        json {
            ... options of openapi-processor-json
        }
        
    }
    
    
The configuration of a single processor has a few pre-defined properties and it can have any number of
additional parameters defined by the processor (all options are passed in a map to the processor with
 the option name as the key).
 
* `processor` (mandatory): the `processor` dependency. Uses the same dependency notations allowed in
 the gradle `dependencies` block.

    The processor library is configured here to avoid any side effect on the build dependencies of the
    project.   

    Example using the preferred shortcut nation:

        spring {
            processor 'com.github.hauner.openapi:openapi-processor-spring:<version>'
        }

  or like this to use an un-published generatr:

        spring {
            processor files('... path to generatr jar')
        }
  
 
* `apiPath` (optional): the path to the open api yaml file. If set inside a processor configuration it
overrides the parent `apiPath`.

* `targetDir` (mandatory): the target folder for the processor. The processor will write its output to
 this directory.

# gradle tasks

The plugin creates a single gradle task for each processor configuration that will run the corresponding
processor. The name is derived from the name of the processor:  `generate<Name>`.


The plugin does not add the `process<Name>` task to the build lifecycle. To run it automatically
add a task dependency in the `build.gradle` file. For example to run openapi-processor-spring before
compiling   

    // generate api before compiling
    compileJava.dependsOn ('processSpring')
    
and to run openapi-processor-json when processing the resources:    
    
    processResources.dependsOn ('processJson')


# using the processor output 

In case the processor creates java sources it is necessary to compile them as part of the build process.

For example to compile the java source files created by openapi-generatr-spring add the `targetDir` of
 the  processor to the java `sourceSets`:

    // add the targetDir of the processor as additional source folder to java.
    sourceSets {
        main {
            java {
                // add generated files
                srcDir 'build/openapi'
            }
        }
    }

To add the json file created by the openapi-processor-json to the final artifact jar as resource add
 the `targetDir` of the processor to the java `resources` source set:


    // add the targetDir of the processor as additional resource folder.
    sourceSets {
        main {
            resources {
                srcDir "$buildDir/json"
            }
        }
    }


# configuration example

Here is a full example using the processors [spring][oap-spring] & [json][oap-json]:

    openapiProcessor {

        // the path to the open api yaml file. Usually the same for all processors.
        //
        apiPath "${projectDir}/src/api/openapi.yaml"

        // based on the name of a processor configuration the plugin creates a gradle task with name
        // "process${name of processor}"  (in this case "processSpring") to run the processor.
        //
        spring {
            // the openapi-processor-spring dependency (mandatory)
            //
            processor 'com.github.hauner.openapi:openapi-processor-spring:1.0.0.Mx'
    
            // setting api path inside a processor configuration overrides the one at the top.
            //
            // apiPath "${projectDir}/src/api/openapi.yaml"
    
            // the destination folder for generating interfaces & models. This is the parent of the
            // {package-name} folder tree configured in the mapping file. (mandatory)
            //
            targetDir "$projectDir/build/openapi"
    
            //// openapi-processor-spring specific options
            
            // file name of the mapping yaml configuration file. Note that the yaml file name must end
            // with either {@code .yaml} or {@code .yml}.
            //
            mapping "${projectDir}/src/api/mapping.yaml"
    
            // show warnings from the open api parser.
            showWarnings true
        }

        // applying the rule described above the task to run this one is "processJson".
        //
        json {
            // the openapi-processor-json dependency (mandatory)
            //
            processor 'com.github.hauner.openapi:openapi-processor-json:1.0.0.Mx'

            // the destination folder for the json file. (mandatory)
            targetDir "$buildDir/json"
        }

    }

# sample project

See [`openapi-generatr-spring-mvc-sample`][oap-spring-mvc] for a complete spring boot sample project.

# plugins.gradle.org

The plugin at the [plugin portal][oap-plugin].

[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[license]: https://github.com/hauner/openapi-processor-gradle/blob/master/LICENSE
[badge-ci]: https://github.com/hauner/openapi-processor-gradle/workflows/ci/badge.svg
[workflow-ci]: https://github.com/hauner/openapi-processor-gradle/actions?query=workflow%3Aci

[oap-plugin]: https://plugins.gradle.org/plugin/com.github.hauner.openapi-processor

[oap-api]: https://github.com/hauner/openapi-processor-api
[oap-json]: https://github.com/hauner/openapi-generatr-json
[oap-spring]: https://github.com/hauner/openapi-generatr-spring
[oap-spring-mvc]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
[oap-spring-gradle]: https://hauner.github.io/openapi-generatr-spring/gradle.html
