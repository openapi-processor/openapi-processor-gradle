package com.github.hauner.openapi.gradle.generatr

import com.github.hauner.openapi.api.OpenApiGeneratr

class IntTestGeneratr implements OpenApiGeneratr<IntTestOptions> {

    @Override
    String getName () {
        'intTest'
    }

    @Override
    Class<IntTestOptions> getOptionsType () {
        IntTestOptions
    }

    @Override
    void run (IntTestOptions options) {
        println "running plugin with an option ${options.anOption}"
    }

}
