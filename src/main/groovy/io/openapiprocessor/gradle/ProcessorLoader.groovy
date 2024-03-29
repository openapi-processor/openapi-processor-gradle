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

package io.openapiprocessor.gradle

/**
 * Find a processor using the ServiceLoader.
 *
 * @author Martin Hauner
 */
class ProcessorLoader {

    static def load(String processorName, ClassLoader classLoader) {
        def processor = findProcessor (processorName, io.openapiprocessor.api.v1.OpenApiProcessor, classLoader)
        if (processor) {
            return processor
        }

        processor = findProcessor (processorName, io.openapiprocessor.api.OpenApiProcessor, classLoader)
        if (processor) {
            return processor
        }

        processor = findProcessor (processorName, com.github.hauner.openapi.api.OpenApiProcessor, classLoader)
        if (processor) {
            return processor
        }

        null
    }

    private static findProcessor(String processorName, Class clazz, ClassLoader classLoader) {
        def processors = []
        processors.addAll(ServiceLoader.load (clazz, classLoader))

        def processor = processors.find {
            it.getName () == processorName
        }

        processor
    }
}
