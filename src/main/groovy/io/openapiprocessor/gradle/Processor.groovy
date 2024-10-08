/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * represents an openapi-processor configured in {@link OpenApiProcessorExtension}
 */
class Processor {
    public static final String API_PATH = 'apiPath'
    public static final String TARGET_DIR = 'targetDir'

    String name
    String config
    def dependencies = [] // String | FileCollection...

    Map<String, Object> other = [:]

    Processor (String configName) {
        this.config = configName
        this.name = configName
    }

    void processorName (String processorName) {
        this.name = processorName
    }

    void processorName (GString processorName) {
        this.name = processorName.toString ()
    }

    void processor (FileCollection fc) {
        dependencies.add (fc)
    }

    void processor (File f) {
        dependencies.add (f)
    }

    void processor (String dep) {
        dependencies.add (dep)
    }

    String getTargetDir () {
        other.get (TARGET_DIR)
    }

    void targetDir (String targetDir) {
        other.put (TARGET_DIR, targetDir)
    }

    void targetDir (GString targetDir) {
        other.put (TARGET_DIR, targetDir.toString ())
    }

    void targetDir(Directory targetDir) {
        other.put (TARGET_DIR, targetDir.toString())
    }

    void targetDir(Provider<Directory> targetDir) {
        other.put (TARGET_DIR, targetDir.get().toString())
    }

    void setTargetDir (String targetDir) {
        other.put (TARGET_DIR, targetDir)
    }

    void setTargetDir (GString targetDir) {
        other.put (TARGET_DIR, targetDir.toString ())
    }

    void setTargetDir(Directory targetDir) {
        other.put (TARGET_DIR, targetDir.toString())
    }

    /**
     * allow to assign targetDir like
     *
     * {@code targetDir = layout.buildDirectory.dir("openapi")}
     *
     * @param targetDir targetDir provider
     */
    void setTargetDir(Provider<Directory> targetDir) {
        other.put (TARGET_DIR, targetDir.get().toString())
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

    void setApiPath (RegularFile apiPath) {
        other.put (API_PATH, apiPath.toString())
    }

    void prop (Map<String, Object> props) {
        other.putAll (props)
    }

    void prop (String key, Object value) {
        if (value instanceof RegularFile) {
            other.put(key, value.toString())
        } else {
            other.put (key, value)
        }
    }

    void prop (GString key, Object value) {
        if (value instanceof RegularFile) {
            other.put(key.toString(), value.toString())
        } else {
            other.put (key.toString(), value)
        }
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
