/*
 * Copyright 2019-2020 the original authors
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

import com.github.hauner.openapi.api.OpenApiProcessor

import javax.inject.Inject

/**
 * Runs the processor with the classes class loader which will include the dependencies from the
 * openapiProcessor configuration.
 *
 * @author Martin Hauner
 */
class OpenApiProcessorWorker implements Runnable {

    private String processorName
    private Map<String, ?> processorProps

    @Inject
    OpenApiProcessorWorker (String processorName, Map<String, ?> processorProps) {
        this.processorName = processorName
        this.processorProps = processorProps
    }

    @Override
    void run () {
        try {
            OpenApiProcessor processor = getProcessor ()
            processor.run (processorProps)
        } catch (Exception e) {
            throw e
        }
    }

    private OpenApiProcessor getProcessor () {
        OpenApiProcessor processor = ProcessorLoader.load (this.class.classLoader).find {
            it.getName () == processorName
        } as OpenApiProcessor
        processor
    }

}
