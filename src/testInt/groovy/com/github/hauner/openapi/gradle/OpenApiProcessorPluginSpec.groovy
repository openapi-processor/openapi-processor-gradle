/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS


class OpenApiProcessorPluginSpec extends Specification {

    @Rule
    TemporaryFolder testProjectDir

    File buildFile
    File openapiFile
    String projectDir

    def setup() {
        projectDir = System.properties['PROJECT_DIR']
        buildFile = testProjectDir.newFile('build.gradle')

        testProjectDir.newFolder ('src', 'api')
        openapiFile = testProjectDir.newFile ('src/api/openapi.yaml')
    }

    @Unroll
    void "generate task calls processor with gradle #gradleVersion" () {
        openapiFile << """\
property: value
"""

        buildFile << """\
plugins {
  id 'io.openapiprocessor.openapi-processor'
}

openapiProcessor {
    apiPath "\${projectDir}/src/api/openapi.yaml"

    v1 {
        processor files("${projectDir}/processor-v1/build/libs/processor-v1.jar")

        targetDir "\${buildDir}/v1"
        v1 "value v1"
    }
    
    one {
        processor files("${projectDir}/processor-one/build/libs/processor-one.jar")

        targetDir "\${buildDir}/one"
        other1 "value one"
    }

    two {
        processor files("${projectDir}/processor-two/build/libs/processor-two.jar")

        targetDir "\${buildDir}/two"
        other2 "value two"
    }
    
}
"""

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('--stacktrace', 'processV1', 'processOne', 'processTwo')
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

        result.task(':processOne').outcome == SUCCESS
        result.output.contains("processor one did run !")

        result.task(':processTwo').outcome == SUCCESS
        result.output.contains("processor two did run !")

        where:
        // 5.5 is the minimum required version to run this test
        gradleVersion << [
            '5.5', '5.5.1',
            '5.6', '5.6.1', '5.6.2', '5.6.3', '5.6.4',
            '6.0', '6.0.1',
            '6.1', '6.1.1',
            '6.2', '6.2.1', '6.2.2',
            '6.3',
            '6.4', '6.4.1',
            '6.5', '6.5.1',
            '6.6', '6.6.1',
            '6.7', '6.7.1',
            '6.8', '6.8.1', '6.8.2', '6.8.3',
            '7.0-20210306230028+0000'
        ].reverse ()
    }

}
