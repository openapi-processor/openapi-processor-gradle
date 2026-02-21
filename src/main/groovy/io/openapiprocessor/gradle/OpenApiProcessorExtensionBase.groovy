/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class OpenApiProcessorExtensionBase {

    /**
     * groovy dsl. create a new processor configuration.
     *
     * <pre>
     *  openapiProcessor {
     *    newProcessor { ... }
     * }
     * </pre>
     *
     * gradle will never call this from a kotlin build script unless explicitly calling it, i.e.
     *
     * <pre>
     *  openapiProcessor {
     *    methodMissing "newProcessor" { ... }
     * }
     * </pre>
     *
     * @param name unique name of the processor
     * @param args arg array. arg[0] must be a {@link ProcessorBase} configuration block
     * @return the new processor
     */
    @CompileDynamic
    def methodMissing(String name, def args) {
        def arg = args[0]

        // expecting a nested processor configuration
        if (arg instanceof Closure) {
            def processor = this.processors.create(name)
            processor.with(arg)
            return processor
        }

        throw new MissingMethodException(name, OpenApiProcessorExtension, args)
    }
}
