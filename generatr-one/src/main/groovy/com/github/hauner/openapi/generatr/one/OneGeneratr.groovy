package com.github.hauner.openapi.generatr.one

import com.github.hauner.openapi.api.OpenApiGeneratr

class OneGeneratr implements OpenApiGeneratr {

    @Override
    String getName () {
        'one'
    }

    @Override
    void run (Map<String, ?> options) {
        println "generatr ${name} did run ! (${options.targetDir})"
    }

}
