/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.gradle

import org.gradle.api.file.FileCollection

/**
 * represents a generatr configured in {@link OpenApiGeneratrExtension}
 *
 * @author Martin Hauner
 */
class Generatr {
    public static final String API_PATH = 'apiPath'
    public static final String TARGET_DIR = 'targetDir'

    String name
    def generatrLib  // String | FileCollection...

    Map<String, Object> other = [:]

    Generatr (String name) {
        this.name = name
    }

    void generatr (FileCollection fc) {
        generatrLib = fc
    }

    void generatr (String dep) {
        generatrLib = dep
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
