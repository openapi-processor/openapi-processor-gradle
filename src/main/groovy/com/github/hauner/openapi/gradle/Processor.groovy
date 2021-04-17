/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.gradle

import org.gradle.api.file.FileCollection

/**
 * backward compatibility, replaced by {@link io.openapiprocessor.gradle.Processor}
 */
@Deprecated
class Processor extends io.openapiprocessor.gradle.Processor {

    Processor (String name) {
        super(name)
    }

}
