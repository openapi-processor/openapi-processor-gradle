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

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Unroll
    void "generate task calls generatr with gradle #gradleVersion" () {
        def intTestOption = "I'm an option!"
        def intTest2Option = "I'm another option!"

        buildFile << """
            plugins {
              id 'com.github.hauner.openapi-generatr'
            }
            
            generatrIntTest {
              anOption = "$intTestOption"
            }

            generatrIntTest2 {
              anotherOption = "$intTest2Option"
            }
        """

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('generateIntTest', 'generateIntTest2')
            .withPluginClasspath ([
                new File("./build/classes/groovy/main/"),
                new File("./build/classes/groovy/testInt/"),
                new File("./build/resources/main/"),
                new File("./build/resources/testInt/")
            ])
            .withDebug (true)
            .build()

        then:
        result.output.contains(intTestOption)
        result.task(':generateIntTest').outcome == SUCCESS
        result.output.contains(intTest2Option)
        result.task(':generateIntTest2').outcome == SUCCESS

        where:
        gradleVersion << ['5.2.1']
    }

}
