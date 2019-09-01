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

import com.github.hauner.openapi.api.OpenApiGeneratr
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Gradle plugin. Currently just provides a task and options object to run any generatr.
 *
 * @author Martin Hauner
 */
class OpenApiGeneratrPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {
        addGeneratrs (loadGeneratrs (project), project)
    }

    private Map<String, GeneratrData> addGeneratrs (LinkedHashMap<String, GeneratrData> generators, project) {
        generators.each { generatorEntry ->
            def name = generatorEntry.key
            def data = generatorEntry.value

            project.task (
                [group: 'openapi', description: "generate api sources with openapi-generatr-$name"],
                "generate${name.capitalize ()}Api") { Task task ->
                doLast {
                    try {
                        runGeneratr (data)
                    } catch (Exception e) {
                        logger.error (task.name, e)
                    }
                }
            }
        }
    }

    private runGeneratr (GeneratrData data) {
        data.generatr.run (data.options)
    }

    private LinkedHashMap<String, GeneratrData> loadGeneratrs (project) {
        Map<String, GeneratrData> generators = [:]

        GeneratrLoader.load ().each { generatr ->
            String name = generatr.name
            Class<?> options = generatr.optionsType
            def extension = project.extensions.create ("generatr${name.capitalize ()}", options)
            generators.put (name, new GeneratrData (generatr: generatr, options: extension))
        }

        generators
    }

    class GeneratrData {
        OpenApiGeneratr generatr
        def /* options extension object */ options
    }
}
