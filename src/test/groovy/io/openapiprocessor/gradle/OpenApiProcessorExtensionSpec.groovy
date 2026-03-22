/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

@SuppressWarnings('ConfigurationAvoidance')
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
        ex.processors.getByName("test").other.one == "a"
        ex.processors.getByName("test").other.two == "b"
        ex.processors.getByName("test2").other.one == "a2"
        ex.processors.getByName("test2").other.two == "b2"
    }

    void "creates processor from string" () {
        when:
        ex.process("test", {
            processor("io.openapiprocessor:processor:1.0.0")
        })

        then:
        def processor = ex.processors.getByName("test")
        def dependencies = processor.dependencies.process.dependencies.get()
            .collect {
                return "${it.group}:${it.name}:${it.version}" as String
            }

        dependencies.first() == "io.openapiprocessor:processor:1.0.0"
    }

    void "creates processor from file" () {
        when:
        ex.process("test") {
            processor(project.file("processor.jar"))
        }

        then:
        def processor = ex.processors.getByName("test")
        def dependencies = processor.dependencies.process.dependencies.get()
            .collect {(it as FileCollectionDependency).files.files }
            .flatten() as List<File>

        dependencies.first().name.endsWith("processor.jar")
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
        ex.processors.getByName("test").other.one == "a"
        ex.processors.getByName("test").other.two == "b"
        ex.processors.getByName("test2").other.one == "a2"
        ex.processors.getByName("test2").other.two == "b2"
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

    void "add processor specific dependency"() {
        when:
        ex.process ("test") {
            dependencies {
                process("com.google.googlejavaformat:google-java-format:1.24.0")
            }
        }

        then:
        def config = project.configurations.getByName("testScope")
        def dependencies = config.dependencies.collect { "${it.group}:${it.name}:${it.version}" as String }

        dependencies.contains("io.openapiprocessor:openapi-processor-api:${Versions.api}" as String)
        dependencies.contains("com.google.googlejavaformat:google-java-format:1.24.0")
    }
}
