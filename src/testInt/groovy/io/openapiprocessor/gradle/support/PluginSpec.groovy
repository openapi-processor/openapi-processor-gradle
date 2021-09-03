/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.support

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class PluginSpec extends Specification {

    @Rule
    TemporaryFolder testProjectDir

    private File buildFile
    private File openapiFile
    private String projectDir

    void setup() {
        projectDir = System.properties ['PROJECT_DIR']
        testProjectDir.newFolder ('src', 'api')

        buildFile = testProjectDir.newFile (getBuildFileName ())
        buildFile << getBuildFile(projectDir)

        openapiFile = testProjectDir.newFile ('src/api/openapi.yaml')
        openapiFile << getOpenApiFile()
    }

    abstract String getBuildFileName()
    abstract String getBuildFile(String projectDir)

    String getOpenApiFile () {
        """\
        property: value
        """.stripIndent ()
    }

    abstract List<String> getGradleArguments()

    BuildResult build(String gradleVersion) {
        return GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments(getGradleArguments ())
            .withPluginClasspath ([
                new File("${projectDir}/build/classes/groovy/main/"),
                new File("${projectDir}/build/classes/java/main/"),
                new File("${projectDir}/build/resources/main/")
            ])
            .withDebug (true)
            .build()
    }

}
