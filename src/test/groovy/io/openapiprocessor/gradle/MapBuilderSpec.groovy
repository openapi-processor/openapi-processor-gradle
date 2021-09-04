/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.slf4j.Logger
import spock.lang.Specification

class MapBuilderSpec extends Specification {

    void "does set simple property value via method" () {
        def builder = new MapBuilder()

        when:
        builder.with {
            prop "value"
        }

        def result = builder.get()

        then:
        result.prop == "value"
    }

    void "does set simple property value via set" () {
        def builder = new MapBuilder()

        when:
        builder.with {
            prop = "value"
        }

        def result = builder.get()

        then:
        result.prop == "value"
    }

    void "warns when property is set multiple times" () {
        def log = Mock Logger
        log.isWarnEnabled () >> true

        def builder = new MapBuilder(log: log)

        when:
        builder.with {
            prop "first-value"
            prop "second-value"
        }

        builder.get()

        then:
        1 * log.warn (*_) >> {
            assert it[1][0] == "prop"
            assert it[1][1] == "first-value"
            assert it[1][2] == "second-value"
            true
        }
    }

    void "does create nested maps for nested dsl closures"() {
        def builder = new MapBuilder()

        when:
        builder.with {
            nested1 {
                nested2 {
                    nested3 {
                    }
                }
            }
        }

        def result = builder.get()

        then:
        result.nested1 instanceof Map
        result.nested1.nested2 instanceof Map
        result.nested1.nested2.nested3 instanceof Map
    }

    void "sets nested properties"() {
        def builder = new MapBuilder()

        when:
        builder.with {
            nested1 {
                prop1 "value1"

                nested2 {
                    prop2 "value2"

                    nested3 {
                        prop3 "value3"
                    }
                }
            }
        }

        def result = builder.get()

        then:
        result.nested1.prop1 == "value1"
        result.nested1.nested2.prop2 == "value2"
        result.nested1.nested2.nested3.prop3 == "value3"
    }

}
