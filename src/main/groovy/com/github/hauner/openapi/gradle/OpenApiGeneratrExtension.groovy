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
 * Extension object of the plugin. Used to configure the generatrs, e.g.
 * <pre>
 * openapiGeneratr {
 *     ...
 *     apiPath "...."
 *
 *     spring {
 *       generatr "..:...:..."
 *       targetDir "..."
 *
 *       .. other
 *     }
 *
 *     json {
 *       generatr "..:...:..."
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
class OpenApiGeneratrExtension {

    /**
     * the path to the openapi yaml file. Used for all generatrs if not set in a nested generatr
     * configuration.
     */
    Property<String> api

    /**
     * properties of the nested generatr configurations by generatr name, e.g.
     * <pre>
     *  openapiGeneratr {
     *
     *      aGeneratr {
     *          generatr "..:...:..."
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
    MapProperty<String, Generatr> generatrs

    private Project project

    OpenApiGeneratrExtension(Project project, ObjectFactory objectFactory) {
        this.project = project
        api = objectFactory.property(String)
        generatrs = objectFactory.mapProperty (String, Generatr)
    }

    def methodMissing (String name, def args) {
        def arg = args[0]

        // should be a nested generatr configuration
        if (arg instanceof Closure) {

            // apply it to a new Generatr () entry
            def generatr = new Generatr (name)
            arg.delegate = generatr

            project.configure (project, wrapWithProjectDelegate (arg))
            generatrs.put (name, generatr as Generatr)
            return generatr
        }

        throw new MissingMethodException(name, OpenApiGeneratrExtension, args)
    }

    /**
     * wraps the given closure with a closure that delegates to the project.
     *
     * this makes it possible to use any of the different dependency formats as value to the
     * 'generatr' property in a generatr configuration block, e.g.
     * <pre>
     *     ...
     *     spring {
     *         generatr files ("../some/lib.jar")
     *         ....
     *     }
     *     ...
     * </pre>
     * or
     * <pre>
     *     ...
     *     spring {
     *         generatr "group:artifact:version"
     *         ....
     *     }
     *     ...
     * </pre>
     *
     * @param generatr configuration closure
     * @return the created wrapper closure
     */
    private static Closure wrapWithProjectDelegate (Closure generatr) {
        return {
            generatr.run ()
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
