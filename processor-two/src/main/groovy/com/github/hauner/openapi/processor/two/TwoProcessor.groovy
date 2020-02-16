package com.github.hauner.openapi.processor.two

import com.github.hauner.openapi.api.OpenApiProcessor

class TwoProcessor implements OpenApiProcessor {

    @Override
    String getName () {
        'two'
    }

    @Override
    void run (Map<String, ?> options) {
        println "processor ${name} did run ! (${options.targetDir})"
    }

}
