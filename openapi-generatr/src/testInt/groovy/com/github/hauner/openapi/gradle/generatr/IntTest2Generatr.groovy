package com.github.hauner.openapi.gradle.generatr

import com.github.hauner.openapi.api.OpenApiGeneratr

class IntTest2Generatr implements OpenApiGeneratr<IntTest2Options> {

    @Override
    String getName () {
        'intTest2'
    }

    @Override
    Class<IntTest2Options> getOptionsType () {
        IntTest2Options
    }

    @Override
    void run (IntTest2Options options) {
        println "running plugin with an option ${options.anotherOption}"
    }

}
