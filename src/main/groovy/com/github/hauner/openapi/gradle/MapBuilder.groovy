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

import groovy.util.logging.Slf4j

/**
 * convert any (nested) dsl structure (ie. closures) to a (nested) map structure.
 *
 * <pre>
 * def builder = new Builder()
 * builder.with {
 *     prop "value"
 *     prop2 = "value"
 *
 *     nested {
 *         otherProp "value"
 *         otherProp2 = "value"
 *     }
 * }
 * def map = builder.get()
 * </pre>
 *
 * @author Martin Hauner
 */
@Slf4j
class MapBuilder {
    private Map<String, ?> props = [:]

    /**
     * returns the converted dsl map.
     *
     * @return the result map
     */
    Map get() {
        return props
    }

    /**
     * adds a key {@code prop} with value {@code "value"} to the result map given a closure expression
     * like this:
     * <pre>
     * { prop "value" }
     * </pre>
     *
     * @param propertyName the property name
     * @param value the property value
     */
    @Override
    Object invokeMethod(String methodName, Object args) {
        def value = args[0]
        if (value instanceof Closure) {
            def builder = new MapBuilder()
            builder.with value
            setProperty (methodName, builder.get ())
        } else {
            setProperty (methodName, value)
        }
    }

    /**
     * adds a key {@code prop} with value {@code "value"} to the result map given a closure expression
     * like this:
     * <pre>
     * { prop = "value" }
     * </pre>
     *
     * @param propertyName the property name
     * @param value the property value
     */
    @Override
    void setProperty(String propertyName, Object value) {
        if (props.containsKey (propertyName)) {
            log.warn ("replacing property {} with value {} to value {}!",
                propertyName, props.get (propertyName), value)
        }

        props.put (propertyName, value)
    }

}
