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

import com.github.hauner.openapi.gradle.support.Sl4jMockRule
import org.junit.Rule
import org.slf4j.Logger
import spock.lang.Specification

class MapBuilderSpec extends Specification {

    def log = Mock Logger
    @Rule Sl4jMockRule rule = new Sl4jMockRule(MapBuilder, log)

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
        log.isWarnEnabled () >> true
        def builder = new MapBuilder()

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
