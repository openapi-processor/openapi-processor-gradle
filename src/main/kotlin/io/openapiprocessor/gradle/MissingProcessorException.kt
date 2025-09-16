/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

class MissingProcessorException(processorName: String)
    : RuntimeException("can't find processor: ${processorName}!")
