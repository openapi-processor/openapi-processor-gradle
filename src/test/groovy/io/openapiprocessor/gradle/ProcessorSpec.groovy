/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

@SuppressWarnings('ConfigurationAvoidance')
class ProcessorSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void "gets & sets named props" () {
        def processor = new Processor('test', project.objects)

        when:
        processor."${prop}" (value)

        then:
        processor."get${prop.capitalize ()}"() == value

        where:
        prop        | value
        'apiPath'   | 'openapi.yaml'
        'targetDir' | 'target folder'
    }

    void "maps unknown properties via methodMissing" () {
        def processor = new Processor('test', project.objects)

        when:
        processor.unknown ('foo')

        then:
        processor.other.unknown == 'foo'
    }

    void "maps nested properties via methodMissing" () {
        def processor = new Processor('test', project.objects)

        when:
        processor.test {
            one "a"
            two "b"
            three {
                four "c"
                five "d"
            }
        }

        then:
        processor.other.test.one == 'a'
        processor.other.test.two == 'b'
        processor.other.test.three.four == 'c'
        processor.other.test.three.five == 'd'
    }

    void "maps unknown properties via prop()" () {
        def processor = new Processor('test', project.objects)

        when:
        processor.prop ('foo', "any")
        processor.prop ("foo2", "any2")

        then:
        processor.other.foo == 'any'
        processor.other.foo2 == 'any2'
    }

    void "maps nested properties via prop()" () {
        def processor = new Processor('test', project.objects)

        when:
        processor.prop("test", [
            one: "a",
            two: "b",
            three: [
                four: "c",
                five: "d"
            ]
        ])

        then:
        processor.other.test.one == 'a'
        processor.other.test.two == 'b'
        processor.other.test.three.four == 'c'
        processor.other.test.three.five == 'd'
    }

    void "accept targetDir when given as Directory" () {
        project.pluginManager.apply(OpenApiProcessorPlugin)

        when:
        project.openapiProcessor {
            process("any") {
                targetDir(project.layout.buildDirectory.dir("openapi").get())
            }
        }

        then:
        def ext = project.extensions.findByType(OpenApiProcessorExtension)
        def processor = ext.processors.getByName("any")

        processor.targetDir == project.layout.buildDirectory.dir("openapi").get().toString()
    }

    void "accept targetDir when given as Provider<Directory>" () {
        project.pluginManager.apply(OpenApiProcessorPlugin)

        when:
        project.openapiProcessor {
            process("any") {
                targetDir(project.layout.buildDirectory.dir("openapi"))
            }
        }

        then:
        def ext = project.extensions.findByType(OpenApiProcessorExtension)
        def processor = ext.processors.getByName("any")

        processor.targetDir == project.layout.buildDirectory.dir("openapi").get().toString()
    }

    void "accept targetDir assignment with Provider<Directory>" () {
        project.pluginManager.apply(OpenApiProcessorPlugin)

        when:
        project.openapiProcessor {
            process("any") {
                targetDir = project.layout.buildDirectory.dir("openapi")
            }
        }

        then:
        def ext = project.extensions.findByType(OpenApiProcessorExtension)
        def processor = ext.processors.getByName("any")

        processor.targetDir == project.layout.buildDirectory.dir("openapi").get().toString()
    }

    void "accept mapping property when given as RegularFile" () {
        project.pluginManager.apply(OpenApiProcessorPlugin)

        when:
        project.openapiProcessor {
            process("any") {
                prop("mapping", project.layout.projectDirectory.file("src/api/mapping.yaml"))
            }
        }

        then:
        def ext = project.extensions.findByType(OpenApiProcessorExtension)
        def processor = ext.processors.getByName("any")

        processor.other.mapping == project.layout.projectDirectory.file("src/api/mapping.yaml").toString()
    }
}
