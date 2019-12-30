package com.github.hauner.openapi.generatr.two

import com.github.hauner.openapi.api.OpenApiGeneratr

class TwoGeneratr implements OpenApiGeneratr {

    @Override
    String getName () {
        'two'
    }

    @Override
    void run (Map<String, ?> options) {
        println "generatr ${name} did run ! (${options.targetDir})"
    }

}
