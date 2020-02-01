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

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

/**
 * OpenAPI Generatr Gradle plugin.
 *
 * @author Martin Hauner
 */
class OpenApiGeneratrPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {
        if (!isSupportedGradleVersion (project)) {
            return
        }

        addOpenApiGeneratrRepository (project)

        def ext = createExtension (project)
        project.afterEvaluate (createTasksBuilderAction (ext))
    }

    private static boolean isSupportedGradleVersion (Project project) {
        String version = project.gradle.gradleVersion

        if (version < "5.2") {
            project.logger.error ("the current gradle version is ${version}")
            project.logger.error ("com.github.hauner.openapi-generatr requires gradle 5.2+")
            return false
        }

        return true
    }

    private static OpenApiGeneratrExtension createExtension (Project project) {
        project.extensions.create ('openapiGeneratr', OpenApiGeneratrExtension, project)
    }

    private addOpenApiGeneratrRepository (Project project) {
        project.repositories {
            mavenCentral()
            maven {
                url "https://dl.bintray.com/hauner/openapi-generatr"
                content {
                   includeGroupByRegex "com\\.github\\.hauner\\.openapi.*"
                }
            }
        }
    }

    /**
     * Provides an Action that create a 'generate{GeneratrName}' task for each configured generatr.
     */
    private Action<Project> createTasksBuilderAction (OpenApiGeneratrExtension extension) {
        new Action<Project>() {

            @Override
            void execute (Project project) {
                registerTasks (project)
            }

            private void registerTasks (Project project) {
                extension.generatrs.get ().each { entry ->
                    def name = "generate${entry.key.capitalize ()}"
                    def action = createTaskBuilderAction (entry.key, entry.value, extension)

                    project.tasks.register (name, OpenApiGeneratrTask, action)
                }
            }
        }
    }

    /**
     * Creates an Action that configures a 'generate{GeneratrName}' task from its configuration.
     */
    private Action<OpenApiGeneratrTask> createTaskBuilderAction(
        String name, Generatr config, OpenApiGeneratrExtension extension) {

        new Action<OpenApiGeneratrTask>()  {

            @Override
            void execute (OpenApiGeneratrTask task) {
                task.setGeneratrName (name)
                task.setGeneratrProps (config.other)

                task.setGroup ('openapi generatr')
                task.setDescription ("generate sources from api with openapi-generatr-$name")

                copyApiPath (task)
                task.setApiDir (getInputDirectory ())
                task.setTargetDir (getOutputDirectory ())

                def project = task.getProject ()
                def handler = project.getDependencies ()
                Dependency api = handler.create("com.github.hauner.openapi:openapi-generatr-api:1.0.0.M3")

                if (!config.generatrLib) {
                    task.logger.warn ("'openapiGeneratr.${name}.generatr' not set!")
                }

                Dependency dep = handler.create (config.generatrLib)

                Configuration cfg = project.getConfigurations ().detachedConfiguration (api, dep)
                cfg.setVisible (false)
                cfg.setTransitive (true)
                cfg.setDescription ("the dependencies of the generate${name.capitalize ()} task.")
                task.dependencies = cfg
            }

            private String getInputDirectory () {
                String path = config.apiPath
                def file = new File (path)
                file.parent
            }

            private String getOutputDirectory () {
                config.targetDir
            }

            // copy common api path to generatr props if not set
            private copyApiPath (OpenApiGeneratrTask task) {
                if (!config.hasApiPath ()) {
                    if (!extension.apiPath.present) {
                        task.logger.warn ("'openapiGeneratr.apiPath' or 'openapiGeneratr.${name}.apiPath' not set!")
                        return
                    }

                    config.apiPath = extension.apiPath.get ()
                }
            }

        }
    }

}
