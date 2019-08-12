package com.github.hauner.openapi.gradle

import com.github.hauner.openapi.api.OpenApiGeneratr

class GeneratrLoader {

    static Iterable<OpenApiGeneratr<?>> load() {
        ServiceLoader<OpenApiGeneratr> generatrs = ServiceLoader.load (OpenApiGeneratr.class)
        if (generatrs.empty) {
            return []
        }
        generatrs
    }

}
