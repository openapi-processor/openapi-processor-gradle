/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import io.openapiprocessor.gradle.support.Gradle
import io.openapiprocessor.gradle.support.PluginSpec
import org.gradle.testkit.runner.BuildResult
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class MultipleConfigurationsSameProcessorSpec extends PluginSpec {

    @Override
    String getBuildFileName () {
        'build.gradle'
    }

    @Override
    String getBuildFile (String projectDir) {
        """\
        plugins {
          id 'io.openapiprocessor.openapi-processor'
        }
        
        repositories {
            mavenCentral()
        }
        
        openapiProcessor {
            firstApi {
                processorName "one"
                processor project.files("${projectDir}/processor-one/build/libs/processor-one.jar")
        
                apiPath "\${projectDir}/src/api/openapi-first.yaml"
                targetDir "\${buildDir}/first"
                other1 "value first"
            }
        
            secondApi {
                processorName "one"
                processor project.files("${projectDir}/processor-one/build/libs/processor-one.jar")
        
                apiPath "\${projectDir}/src/api/openapi-second.yaml"
                targetDir "\${buildDir}/second"
                other2 "value second"
            }
        }
        """.stripIndent ()
    }

    @Override
    List<String> getGradleArguments () {
        ['--stacktrace', 'processFirstApi', 'processSecondApi']
    }

    @Unroll
    void "process task runs processor from gradle 7 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        assertResult (result)

        where:
        gradleVersion << Gradle.VERSIONS_7.reverse ()
    }

    @Unroll
    void "process task runs processor from gradle 8 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        assertResult (result)

        where:
        gradleVersion << Gradle.VERSIONS_8.reverse ()
    }

    private void assertResult(BuildResult result) {
        assert result.task(':processFirstApi').outcome == SUCCESS
        assert result.output.contains("processor one did run !")

        assert result.task(':processSecondApi').outcome == SUCCESS
        assert result.output.contains("processor one did run !")
    }

}
