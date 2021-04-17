/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.gradle

import org.gradle.api.Project
import org.gradle.api.internal.provider.DefaultMapProperty
import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.model.ObjectFactory
import spock.lang.Specification


class OpenApiProcessorExtensionSpec extends Specification {

    def project = Mock (Project)
    def objectFactory = Mock (ObjectFactory)

    void setup () {
        objectFactory.property (String) >> new DefaultProperty<String>(null, String)
        objectFactory.mapProperty (String, Processor) >> new DefaultMapProperty<String, Object>(null, String, Processor)
    }

    void "initializes properties" () {
        when:
        def ex = new OpenApiProcessorExtension (project, objectFactory)

        then:
        ex.apiPath.value ("openapi.yaml")
        ex.apiPath.get () == "openapi.yaml"
    }

    void "converts processor closure to map via methodMissing" () {
        project.configure (_, _) >> { args ->
            Closure c = args[1]
            c.delegate = args[0]
            c.run ()
        }

        when:
        def ex = new OpenApiProcessorExtension (project, objectFactory)
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

    void "converts processor properties to map via process()/prop() methods" () {
        when:
        def ex = new OpenApiProcessorExtension (project, objectFactory)
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

    void "handle apiPath when given as GString" () {
        def projectDir = "projectDir"

        when:
        def ex = new OpenApiProcessorExtension (project, objectFactory)
        ex.apiPath "${projectDir}/src/api/openapi.yaml"

        then:
        ex.apiPath.get () == 'projectDir/src/api/openapi.yaml'
    }
}
