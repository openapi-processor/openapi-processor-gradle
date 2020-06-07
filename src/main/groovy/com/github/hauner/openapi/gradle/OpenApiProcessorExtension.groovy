/*
 * Copyright 2019-2020 the original authors
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

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * Extension object of the plugin. Used to configure the processors, e.g.
 * <pre>
 * openapiProcessor {
 *     ...
 *     apiPath "...."
 *
 *     spring {
 *       processor "..:...:..."
 *       targetDir "..."
 *
 *       .. other
 *     }
 *
 *     json {
 *       processor "..:...:..."
 *       targetDir "..."
 *
 *       .. other
 *     }
 *     ...
 * }
 * </pre>
 *
 * @author Martin Hauner
 */
class OpenApiProcessorExtension {

    /**
     * the path to the openapi yaml file. Used for all processors if not set in a nested processor
     * configuration.
     */
    Property<String> api

    /**
     * properties of the nested processor configurations by processor name, e.g.
     * <pre>
     *  openapiProcessor {
     *
     *      aProcessor {
     *          processor "..:...:..."
     *          targetDir "..."
     *
     *       .. other
     *          prop "abc"
     *          prop "xyz"
     *      }
     *
     *  }
     * </pre>
     */
    MapProperty<String, Processor> processors

    private Project project

    OpenApiProcessorExtension (Project project, ObjectFactory objectFactory) {
        this.project = project
        api = objectFactory.property(String)
        processors = objectFactory.mapProperty (String, Processor)
    }

    def methodMissing (String name, def args) {
        def arg = args[0]

        // should be a nested processor configuration
        if (arg instanceof Closure) {

            // apply it to a new Processor () entry
            def processor = new Processor (name)
            arg.delegate = processor

            project.configure (project, wrapWithProjectDelegate (arg))
            processors.put (name, processor)
            return processor
        }

        throw new MissingMethodException(name, OpenApiProcessorExtension, args)
    }

    /**
     * wraps the given closure with a closure that delegates to the project.
     *
     * this makes it possible to use any of the different dependency formats as value to the
     * 'processor' property in a processor configuration block, e.g.
     * <pre>
     *     ...
     *     spring {
     *         processor files ("../some/lib.jar")
     *         ....
     *     }
     *     ...
     * </pre>
     * or
     * <pre>
     *     ...
     *     spring {
     *         processor "group:artifact:version"
     *         ....
     *     }
     *     ...
     * </pre>
     *
     * @param processor configuration closure
     * @return the created wrapper closure
     */
    private static Closure wrapWithProjectDelegate (Closure processr) {
        return {
            processr.run ()
        }
    }

    void apiPath (String apiPath) {
        api.set (apiPath)
    }

    void apiPath (GString apiPath) {
        api.set (apiPath)
    }

    void setApiPath (String apiPath) {
        api.set (apiPath)
    }

    void setApiPath (GString apiPath) {
        api.set (apiPath)
    }

    Property<String> getApiPath () {
        api
    }

}