/*
 * Copyright 2020 the original authors
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

import spock.lang.Specification

class ProcessorSpec extends Specification {

    void "gets & sets named props" () {
        def processor = new Processor('test')

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
        def processor = new Processor('test')

        when:
        processor.unknown ('foo')

        then:
        processor.other.unknown == 'foo'
    }

    void "maps nested properties via methodMissing" () {
        def processor = new Processor('test')

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
        def processor = new Processor('test')

        when:
        processor.prop ('foo', "any")
        processor.prop ("foo2", "any2")

        then:
        processor.other.foo == 'any'
        processor.other.foo2 == 'any2'
    }

    void "maps nested properties via prop()" () {
        def processor = new Processor('test')

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

}
