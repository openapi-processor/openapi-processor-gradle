/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class OpenApiProcessorExtensionSpec extends Specification {

    Project project
    OpenApiProcessorExtension ex

    void setup () {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(OpenApiProcessorPlugin)
        ex = project.extensions.findByType(OpenApiProcessorExtension)
    }

    void "initializes properties" () {
        ex.apiPath("openapi.yaml")

        expect:
        ex.api.get().asFile == project.file("openapi.yaml")
        ex.checkUpdates.get() == "never"
    }

    void "converts processor closure to map via methodMissing" () {
        when:
        ex.test {
            one "a"
            two "b"
        }
        ex.test2 {
            one "a2"
            two "b2"
        }

        then:
        ex.processors.get ().test.other.one == "a"
        ex.processors.get ().test.other.two == "b"
        ex.processors.get ().test2.other.one == "a2"
        ex.processors.get ().test2.other.two == "b2"
    }

    void "creates processor from string" () {
        when:
        ex.process("test", {
            processor("/a processor")
        })

        then:
        ex.processors.get().test.dependencies.first() == "/a processor"
    }

    void "creates processor from file" () {
        when:
        ex.process("test") {
            processor(project.file("processor"))
        }

        then:
        ex.processors.get().test.dependencies.first().name == "processor"
    }

    void "converts processor properties to map via process()/prop() methods" () {
        when:
        ex.process ("test") {
            it.prop ("one", "a")
            it.prop ("two", "b")
        }
        ex.process ("test2") {
            it.prop ("one", "a2")
            it.prop ("two", "b2")
        }

        then:
        ex.processors.get ().test.other.one == "a"
        ex.processors.get ().test.other.two == "b"
        ex.processors.get ().test2.other.one == "a2"
        ex.processors.get ().test2.other.two == "b2"
    }

    void "set apiPath from GString" () {
        def projectDir = "projectDir"

        when:
        project.openapiProcessor {
            apiPath("${projectDir}/src/api/openapi.yaml")
        }

        then:
        ex.api.get().asFile == project.file('projectDir/src/api/openapi.yaml')
    }

    void "assign apiPath from GString" () {
        def projectDir = "projectDir"

        when:
        project.openapiProcessor {
            apiPath = project.file("${projectDir}/src/api/openapi.yaml")
        }

        then:
        ex.api.get().asFile == project.file('projectDir/src/api/openapi.yaml')
    }

    void "assign apiPath from RegularFile" () {
        when:
        project.openapiProcessor {
            apiPath = project.layout.projectDirectory.file("src/api/openapi.yaml")
        }

        then:
        ex.api.get().asFile == project.file("src/api/openapi.yaml")
    }
}
