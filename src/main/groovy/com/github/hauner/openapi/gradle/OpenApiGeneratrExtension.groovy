/*
 * Copyright 2019 the original authors
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

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * Extension object of the plugin, ie.
 * <pre>
 * openapiGeneratr {
 *     ...
 *     // generatr settings
 *     ...
 * }
 * </pre>
 *
 * @author Martin Hauner
 */
class OpenApiGeneratrExtension {

    /**
     * the path to the openapi yaml file.
     */
    Property<String> apiPath

    /**
     * properties of nested generatr closures, e.g.
     * <pre>
     *  openapiGeneratr {
     *
     *      aGeneratr {
     *          prop "abc"
     *          prop "xyz"
     *      }
     *
     *  }
     * </pre>
     */
    MapProperty<String, Map> generatrs


    OpenApiGeneratrExtension(ObjectFactory objectFactory) {
        apiPath = objectFactory.property(String)
        generatrs = objectFactory.mapProperty (String, Map)
    }

    def methodMissing(String name, def args) {
        // apiPath may be a GString in a groovy build script
        if (name == 'apiPath') {
            apiPath.set (args[0].toString ())
            return
        }

        def builder = new MapBuilder()
        builder.with (args[0] as Closure)
        generatrs.put (name, builder.get ())
    }

}
