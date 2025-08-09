/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import com.github.hauner.openapi.api.OpenApiProcessor
import io.openapiprocessor.api.OpenApiProcessor as OpenApiProcessorV0
import io.openapiprocessor.api.v1.OpenApiProcessor as OpenApiProcessorV1
import io.openapiprocessor.api.v2.OpenApiProcessor as OpenApiProcessorV2
import java.util.ServiceLoader

/**
 * Find a processor using the ServiceLoader.
 */
class ProcessorLoader {
    companion object {
        fun load(processorName: String, classLoader: ClassLoader): Any? {
            val processors2 = ServiceLoader.load (OpenApiProcessorV2::class.java, classLoader)
            val processor2 = processors2.find { it.name == processorName }
            if (processor2 != null) {
                return processor2
            }

            val processors1 = ServiceLoader.load (OpenApiProcessorV1::class.java, classLoader)
            val processor1 = processors1.find { it.name == processorName }
            if (processor1 != null) {
                return processor1
            }

            val processors0 = ServiceLoader.load (OpenApiProcessorV0::class.java, classLoader)
            val processor0 = processors0.find { it.name == processorName }
            if (processor0 != null) {
                return processor0
            }

            val processors = ServiceLoader.load (OpenApiProcessor::class.java, classLoader)
            val processor = processors.find { it.name == processorName }
            if (processor != null) {
                return processor
            }

            return null
        }
    }
}
