/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Action

@CompileStatic
class ClosureAction<T> implements Action<T> {
    private final Closure<T> closure

    ClosureAction(Closure<T> closure) {
        this.closure = closure
    }

    @Override
    void execute(T t) {
        closure.call(t)
    }
}
