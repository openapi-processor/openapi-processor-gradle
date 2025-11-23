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

class MultipleConfigurationsDifferentProcessorsSpec extends PluginSpec {

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
            apiPath "\${projectDir}/src/api/openapi.yaml"
        
            v1 {
                processor project.files("${projectDir}/processor-v1/build/libs/processor-v1.jar")
        
                targetDir "\${buildDir}/v1"
                v1 "value v1"
            }
            
            one {
                processor project.files("${projectDir}/processor-one/build/libs/processor-one.jar")
        
                targetDir "\${buildDir}/one"
                other1 "value one"
            }
        
            two {
                processor project.files("${projectDir}/processor-two/build/libs/processor-two.jar")
        
                targetDir "\${buildDir}/two"
                other2 "value two"
            }
        }
        """.stripIndent ()
    }

    @Override
    List<String> getGradleArguments () {
        ['--stacktrace', 'processV1', 'processOne', 'processTwo']
    }

    @Unroll
    void "process task runs processor from gradle 7 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        assertResult (result)

        where:
        gradleVersion << Gradle.VERSIONS_7
                .findAll { it.groovy }
                .collect { it.version }
    }

    @Unroll
    void "process task runs processor from gradle 8 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        assertResult (result)

        where:
        gradleVersion << Gradle.VERSIONS_8
                .findAll { it.groovy }
                .collect { it.version }
    }

    @Unroll
    void "process task runs processor from gradle 9 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        assertResult (result)

        where:
        gradleVersion << Gradle.VERSIONS_9
                .findAll { it.groovy }
                .collect { it.version }
    }

    private void assertResult(BuildResult result) {
        assert result.task(':processV1').outcome == SUCCESS
        assert result.output.contains("processor v1 did run !")

        assert result.task(':processOne').outcome == SUCCESS
        assert result.output.contains("processor one did run !")

        assert result.task(':processTwo').outcome == SUCCESS
        assert result.output.contains("processor two did run !")
    }

}
