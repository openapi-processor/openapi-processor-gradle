/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class KotlinDslSpec extends Specification {

    @Rule
    TemporaryFolder testProjectDir

    File buildFile
    File openapiFile
    String projectDir

    def setup() {
        projectDir = System.properties['PROJECT_DIR']
        buildFile = testProjectDir.newFile('build.gradle.kts')

        testProjectDir.newFolder ('src', 'api')
        openapiFile = testProjectDir.newFile ('src/api/openapi.yaml')
    }

    @Unroll
    void "test kotlin dsl with with gradle #gradleVersion" () {
        openapiFile << """\
property: value
"""

        buildFile << """\
plugins {
    id("io.openapiprocessor.openapi-processor")
}

openapiProcessor {
    apiPath("\$projectDir/src/api/openapi.yaml")

    process("v1") {
        processor(files("$projectDir/processor-v1/build/libs/processor-v1.jar"))
        targetDir("\$buildDir/v1")

        prop("v1", "value v1")
    }
    
}
"""

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('--stacktrace', 'processV1')
            .withPluginClasspath ([
                new File("${projectDir}/build/classes/groovy/main/"),
                new File("${projectDir}/build/classes/java/main/"),
                new File("${projectDir}/build/resources/main/")
            ])
            .withDebug (true)
            .build()

        then:
        result.task(':processV1').outcome == SUCCESS
        result.output.contains("processor v1 did run !")

        where:
        // 5.5 is the minimum required version to run this test
        gradleVersion << [
//            // does not work with gradle before 6.5
//            '5.5', '5.5.1',
//            '5.6', '5.6.1', '5.6.2', '5.6.3', '5.6.4',
//            '6.0', '6.0.1',
//            '6.1', '6.1.1',
//            '6.2', '6.2.1', '6.2.2',
//            '6.3',
//            '6.4', '6.4.1',
            '6.5', '6.5.1',
            '6.6', '6.6.1',
            '6.7', '6.7.1',
            '6.8', '6.8.1', '6.8.2', '6.8.3',
            '7.0'
        ].reverse ()

    }

}
