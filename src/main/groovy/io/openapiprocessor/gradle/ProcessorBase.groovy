/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

/**
 * groovy dsl. represents an openapi-processor configured in {@link OpenApiProcessorExtension}
 */
@CompileStatic
class ProcessorBase {

    @CompileDynamic
    def methodMissing(String name, def args) {
        if (args[0] instanceof Closure) {
            def builder = new MapBuilder()
            builder.with(args[0] as Closure)
            other.put(name, builder.get())
        } else {
            other.put(name, args[0])
        }
    }
}
