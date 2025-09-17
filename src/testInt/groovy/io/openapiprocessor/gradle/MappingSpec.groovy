/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle


import io.openapiprocessor.gradle.support.PluginSpec

class MappingSpec extends PluginSpec {

    @Override
    String getBuildFileName () {
        'build.gradle.kts'
    }

    @Override
    String getBuildFile (String projectDir) {
        """\
        import io.openapiprocessor.gradle.OpenApiProcessorTask

        plugins {
            id("io.openapiprocessor.openapi-processor")
        }
        
        repositories {
            mavenCentral()
        }
        
        openapiProcessor {
            apiPath(file("src/api/openapi.yaml"))
        
            process("v1") {
                processor(project.files("$projectDir/processor-v1/build/libs/processor-v1.jar"))
                targetDir(layout.buildDirectory.dir("v1"))
        
                prop("mapping", file("mapping.yaml"))
            }
        }
        
        afterEvaluate {
            val task = tasks.named("processV1").get() as OpenApiProcessorTask
            
            println("MAPPING:")
            val mapping = task.getMapping()
            println(mapping)

            val mapping2 = mapping.get()
            println(mapping2)
        }
        
        """.stripIndent ()
    }

    @Override
    List<String> getGradleArguments () {
        ['--stacktrace', '--debug']
    }

    void "passes mapping to task if set" () {
        when:
        def result = build("9.0.0", """\
            """.stripIndent())

        then:
        def mapping = new File(testProjectDir, "mapping.yaml")
        result.output.contains(mapping.absolutePath)
    }
}
