/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
 */
@CompileStatic
class MapBuilder {
    Logger log = LoggerFactory.getLogger (MapBuilder)

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
        def value = (args as Collection<Object>)[0]
        if (value instanceof Closure) {
            def builder = new MapBuilder()
            builder.with value
            setProperty (methodName, builder.get ())
        } else {
            setProperty (methodName, value)
        }

        return null
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
