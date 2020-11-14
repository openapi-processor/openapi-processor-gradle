package io.openapiprocessor.processor

import io.openapiprocessor.api.v1.OpenApiProcessor

class ProcessorV1 implements OpenApiProcessor {

    @Override
    String getName () {
        'v1'
    }

    @Override
    void run (Map<String, ?> options) {
        println "processor ${name} did run ! (${options.targetDir})"
    }

}
