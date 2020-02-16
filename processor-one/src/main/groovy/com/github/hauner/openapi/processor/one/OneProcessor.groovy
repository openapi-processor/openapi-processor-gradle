package com.github.hauner.openapi.processor.one

import com.github.hauner.openapi.api.OpenApiProcessor

class OneProcessor implements OpenApiProcessor {

    @Override
    String getName () {
        'one'
    }

    @Override
    void run (Map<String, ?> options) {
        println "processor ${name} did run ! (${options.targetDir})"
    }

}
