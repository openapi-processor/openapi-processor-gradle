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


class OpenApiGeneratrPluginFuncSpec extends Specification {

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
    void "generate task calls generatr with gradle #gradleVersion" () {
        openapiFile << """\
property: value
"""

        buildFile << """\
plugins {
  id 'com.github.hauner.openapi-generatr'
}

openapiGeneratr {
    apiPath "\${projectDir}/src/api/openapi.yaml"

    one {
        generatr files("${projectDir}/generatr-one/build/libs/generatr-one.jar")

        targetDir "\${buildDir}/one"
        other1 "value one"
    }

    two {
        generatr files("${projectDir}/generatr-two/build/libs/generatr-two.jar")

        targetDir "\${buildDir}/two"
        other2 "value two"
    }
    
}
"""

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('--stacktrace', 'generateOne', 'generateTwo')
            .withPluginClasspath ([
                new File("${projectDir}/build/classes/groovy/main/"),
                new File("${projectDir}/build/resources/main/")
            ])
            .withDebug (true)
            .build()

        then:
        result.task(':generateOne').outcome == SUCCESS
        result.output.contains("generatr one did run !")

        result.task(':generateTwo').outcome == SUCCESS
        result.output.contains("generatr one did run !")

        where:
        // minimum required version 5.2
        gradleVersion << [
            '5.2', '5.2.1',
            '5.3', '5.3.1',
            '5.4', '5.4.1',
            '5.5', '5.5.1',
            '5.6', '5.6.1', '5.6.2', '5.6.3', '5.6.4',
            '6.0', '6.0.1', '6.1', '6.1.1'
        ]
    }

}
