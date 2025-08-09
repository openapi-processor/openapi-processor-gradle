/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import io.openapiprocessor.gradle.support.Gradle
import io.openapiprocessor.gradle.support.PluginSpec
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class KotlinDslSpec extends PluginSpec {

    @Override
    String getBuildFileName () {
        'build.gradle.kts'
    }

    @Override
    String getBuildFile (String projectDir) {
        """\
        plugins {
            id("io.openapiprocessor.openapi-processor")
        }
        
        repositories {
            mavenCentral()
        }
        
        openapiProcessor {
            apiPath("\$projectDir/src/api/openapi.yaml")
        
            process("v1") {
                processor(project.files("$projectDir/processor-v1/build/libs/processor-v1.jar"))
                targetDir("\$buildDir/v1")
        
                prop("v1", "value v1")
            }
        }
        """.stripIndent ()
    }

    @Override
    List<String> getGradleArguments () {
        ['--stacktrace', 'processV1']
    }

    @Unroll
    void "test kotlin dsl with with gradle 7 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        result.task(':processV1').outcome == SUCCESS
        result.output.contains("processor v1 did run !")

        where:
        gradleVersion << Gradle.VERSIONS_7.reverse ()
    }

    @Unroll
    void "test kotlin dsl with with gradle 8 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        result.task(':processV1').outcome == SUCCESS
        result.output.contains("processor v1 did run !")

        where:
        gradleVersion << Gradle.VERSIONS_8.reverse ()
    }

    @Unroll
    void "test kotlin dsl with with gradle 9 (#gradleVersion)" () {
        when:
        def result = build(gradleVersion)

        then:
        result.task(':processV1').outcome == SUCCESS
        result.output.contains("processor v1 did run !")

        where:
        gradleVersion << Gradle.VERSIONS_9.reverse ()
    }
}
