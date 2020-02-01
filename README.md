[![][badge-license]][generatr-license]
[![][badge-ci]][workflow-ci]

# com.github.hauner.openapi.gradle 

a gradle plugin based on the [openapi-generatr-api][generatr-api] to handle all configured openapi-generatrs
without explicit dependency on a generatr. Requires Gradle 5.2 or better.

# gradle dsl

For a more detailed description see [`Using Gradle`][generatr-spring-gradle] in the documentation of
 [openapi-generatr-spring][generatr-spring].

The plugin adds a new configuration block `openapiGeneratr` to the gradle project. Each generatr is
configured by a nested configuration block.
 
Apart from that there is only a single option that is recognized inside the configuration block:

* `apiPath`, which defines the path to the openapi yaml file. This is usually the same for all
generatrs and placing it directly into the the `openapiGeneratr` block sets it for all generatrs.

To configure a generatr, for example the [openapi spring generatr][generatr-spring], a `spring`
configuration is placed into the the `openapiGeneratr` block. The name of the configuration is
used to create a gradle task `generate<Name>` to run the corresponding generatr.


    openapiGeneratr {

        // the path to the open api yaml file.
        apiPath "${projectDir}/src/api/openapi.yaml"
        
        spring {
            ... options of the spring generatr
        }
        
    }
    
    
In case the json generatr is needed it is added in the same way:


    openapiGeneratr {

        // the path to the open api yaml file.
        apiPath "${projectDir}/src/api/openapi.yaml"
        
        spring {
            ... options of the spring generatr
        }

        json {
            ... options of the json generatr
        }
        
    }
    
    
The configuration of a single generatr has a few pre-defined properties and it can have any number of
additional parameters defined by the generatr (all options are passed in a map to the generatr with
 the option name as the key).
 
* `generatr` (mandatory): the `generatr` dependency. Uses the same dependency notations allowed in the
gradle `dependencies` block.

    The generatr library is configured here to avoid any side effect on the build dependencies of the
    project.   

    Example using the preferred shortcut nation:

        spring {
            generatr 'com.github.hauner.openapi:openapi-generatr-spring:1.0.0.M7'
        }

  or like this to use an un-published generatr:

        spring {
            generatr files('... path to generatr jar')
        }
  
 
* `apiPath` (optional): the path to the open api yaml file. If set inside a generatr configuration it
overrides the parent `apiPath`.

* `targetDir` (mandatory): the target folder for the generatr. The generatr will write its output to
 this directory.

# gradle tasks

The plugin creates a single gradle task for each generatr configuration that will run the corresponding
generatr. The name is derived from the generatr name:  `generate<Name>`.


The plugin does not add the `generate<Name>` task to the build lifecycle. To run it automatically
add a task dependency in the `build.gradle` file. For example to run generatr-spring before compiling   

    // generate api before compiling
    compileJava.dependsOn ('generateSpring')
    
and to run generatr-json when processing the resources:    
    
    processResources.dependsOn ('generateJson')


# using the generatr output 

In case the generatr creates java sources it is necessary to compile them as part of the build process.

For example to compile the java source files created by generatr-spring add the `targetDir` of the 
generatr to the java `sourceSets`:

    // add the targetDir of the generatr as additional source folder to java.
    sourceSets {
        main {
            java {
                // add generated files
                srcDir 'build/openapi'
            }
        }
    }

To add the json file created by the generatr-json to the final artifact jar as resource add the
`targetDir` of the generatr to the java `resources` source set:


    // add the targetDir of the generatr as additional resource folder.
    sourceSets {
        main {
            resources {
                srcDir "$buildDir/json"
            }
        }
    }


# configuration example

Here is a full example using the generatrs [spring][generatr-spring] & [json][generatr-json]:

    openapiGeneratr {

        // the path to the open api yaml file. Usually the same for all generatrs.
        //
        apiPath "${projectDir}/src/api/openapi.yaml"

        // based on the name of a generatr configuration the plugin creates a gradle task with name
        // "generate${name of generator}"  (in this case "generateSpring") to run the generatr.
        //
        spring {
            // the spring generatr dependency (mandatory)
            //
            generatr 'com.github.hauner.openapi:openapi-generatr-spring:1.0.0.M7'
    
            // setting api path inside a generatr configuration override the one at the top.
            //
            // apiPath "${projectDir}/src/api/openapi.yaml"
    
            // the destination folder for generating interfaces & models. This is the parent of the
            // {package-name} folder tree configured in the mapping file. (mandatory)
            //
            targetDir "$projectDir/build/openapi"
    
            //// generatr-spring specific options
            
            // file name of the mapping yaml configuration file. Note that the yaml file name must end
            // with either {@code .yaml} or {@code .yml}.
            //
            mapping = "$projectDir/openapi-generatr-spring.yaml"
    
            // show warnings from the open api parser.
            showWarnings true
        }

        // applying the rule described above the task to run this one is "generateJson".
        //
        json {
            // the json generatr dependency (mandatory)
            //
            generatr 'com.github.hauner.openapi:openapi-generatr-json:1.0.0.M2'

            // the destination folder for the json file. (mandatory)
            targetDir "$buildDir/json"
        }

    }

# sample project

See [`openapi-generatr-spring-mvc-sample`][generatr-spring-mvc] for a complete spring boot sample project.

# plugins.gradle.org

The plugin at the [plugin portal][generatr-plugin].

[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[generatr-license]: https://github.com/hauner/openapi-generatr-gradle/blob/master/LICENSE
[badge-ci]: https://github.com/hauner/openapi-generatr-gradle/workflows/ci/badge.svg
[workflow-ci]: https://github.com/hauner/openapi-generatr-gradle/actions?query=workflow%3Aci

[generatr-plugin]: https://plugins.gradle.org/plugin/com.github.hauner.openapi-generatr

[generatr-api]: https://github.com/hauner/openapi-generatr-api
[generatr-spring]: https://github.com/hauner/openapi-generatr-spring
[generatr-json]: https://github.com/hauner/openapi-generatr-json
[generatr-spring-mvc]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
[generatr-spring-gradle]: https://hauner.github.io/openapi-generatr-spring/gradle.html
