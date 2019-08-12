package com.github.hauner.openapi.gradle.generatr

import com.github.hauner.openapi.api.OpenApiGeneratr

class TestGeneratr implements OpenApiGeneratr {

    String name
    Class<?> options

    @Override
    String getName () {
        name
    }

    @Override
    Class<?> getOptionsType () {
        options
    }

    @Override
    void run (def options) {

    }

}
