/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.gradle

import org.gradle.api.file.FileCollection

/**
 * represents an openapi-processor configured in {@link OpenApiProcessorExtension}
 */
class Processor {
    public static final String API_PATH = 'apiPath'
    public static final String TARGET_DIR = 'targetDir'

    String name
    def dependencies = [] // String | FileCollection...

    Map<String, Object> other = [:]

    Processor (String name) {
        this.name = name
    }

    void processor (FileCollection fc) {
        dependencies.add (fc)
    }

    void processor (String dep) {
        dependencies.add (dep)
    }

    void targetDir (String targetDir) {
        other.put (TARGET_DIR, targetDir)
    }

    void targetDir (GString targetDir) {
        other.put (TARGET_DIR, targetDir.toString ())
    }

    String getTargetDir () {
        other.get (TARGET_DIR)
    }

    void apiPath (String apiPath) {
        other.put (API_PATH, apiPath)
    }

    void apiPath (GString apiPath) {
        other.put (API_PATH, apiPath.toString ())
    }

    boolean hasApiPath () {
        other.containsKey (API_PATH)
    }

    String getApiPath () {
        other.get (API_PATH)
    }

    void setApiPath (String path) {
        other.put (API_PATH, path)
    }

    void prop (Map<String, Object> props) {
        other.putAll (props)
    }

    void prop (String key, Object value) {
        other.put (key, value)
    }

    void prop (GString key, Object value) {
        other.put (key.toString (), value)
    }

    def methodMissing (String name, def args) {
        if (args[0] instanceof Closure) {
            def builder = new MapBuilder()
            builder.with (args[0] as Closure)
            other.put (name, builder.get ())
        } else {
            other.put (name, args[0])
        }
    }

}
