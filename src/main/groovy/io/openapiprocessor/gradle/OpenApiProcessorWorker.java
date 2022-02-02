/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle;

import org.gradle.workers.WorkAction;

import java.util.Map;

/**
 * Runs the processor with the class loader that includes the dependencies from the openapiProcessor
 * configuration.
 */
@SuppressWarnings ("deprecation")
abstract public class OpenApiProcessorWorker implements WorkAction<OpenApiProcessorWorkParameters> {

    @Override
    public void execute () {
        Object processor = getProcessor (getProcessorName ());
        Map<String, ?> properties = getProcessorProperties ();

        if (processor instanceof io.openapiprocessor.api.v1.OpenApiProcessor) {
            run ((io.openapiprocessor.api.v1.OpenApiProcessor) processor, properties);

        } else if (processor instanceof io.openapiprocessor.api.OpenApiProcessor) {
            run ((io.openapiprocessor.api.OpenApiProcessor) processor, properties);

        } else if (processor instanceof com.github.hauner.openapi.api.OpenApiProcessor) {
            run((com.github.hauner.openapi.api.OpenApiProcessor) processor, properties);
        }
    }

    private void run (io.openapiprocessor.api.v1.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private void run (io.openapiprocessor.api.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private void run (com.github.hauner.openapi.api.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private Object getProcessor (String processorName) {
        Object processor = ProcessorLoader.load (processorName, getClass ().getClassLoader ());
        if (processor == null) {
            throw new MissingProcessorException(processorName);
        }
        return processor;
    }

    private String getProcessorName() {
        return getParameters ().getProcessorName ().get ();
    }

    private Map<String, ?> getProcessorProperties() {
        return getParameters ().getProcessorProps ().get ();
    }
}
