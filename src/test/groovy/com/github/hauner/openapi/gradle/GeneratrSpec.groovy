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

class GeneratrSpec extends Specification {
    
    void "gets & sets named props" () {
        def generatr = new Generatr('test')
        
        when:
        generatr."${prop}" (value)
        
        then:
        generatr."get${prop.capitalize ()}"() == value

        where:
        prop        | value
        'apiPath'   | 'openapi.yaml'
        'targetDir' | 'target folder'
    }

    void "maps unknown properties" () {
        def generatr = new Generatr('test')

        when:
        generatr.unknown ('foo')

        then:
        generatr.other.unknown == 'foo'
    }

    void "maps nested properties" () {
        def generatr = new Generatr('test')

        when:
        generatr.test {
            one "a"
            two "b"
            three {
                four "c"
                five "d"
            }
        }

        then:
        generatr.other.test.one == 'a'
        generatr.other.test.two == 'b'
        generatr.other.test.three.four == 'c'
        generatr.other.test.three.five == 'd'
    }

}
