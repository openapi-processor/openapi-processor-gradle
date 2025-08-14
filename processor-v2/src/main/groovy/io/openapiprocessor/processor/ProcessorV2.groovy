package io.openapiprocessor.processor

import io.openapiprocessor.api.v2.OpenApiProcessor

class ProcessorV2 implements OpenApiProcessor {

    @Override
    String getName () {
        'v2'
    }

    @Override
    void run (Map<String, ?> options) {
        println "processor ${name} did run ! (${options.targetDir})"
    }

}
