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

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet

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

        def cfg = createConfiguration (project)
        def ext = createExtension (project)

        addRepository (project)
        addDependency (project, cfg)

        project.afterEvaluate (createTasksBuilderAction (ext))
    }

    private boolean isSupportedGradleVersion (Project project) {
        String version = project.gradle.gradleVersion

        if (version < "5.2") {
            project.logger.error ("the current gradle version is ${version}")
            project.logger.error ("com.github.hauner.openapi-generatr requires gradle 5.2+")
            return false
        }

        return true
    }

    private OpenApiGeneratrExtension createExtension (Project project) {
        project.extensions.create ('openapiGeneratr', OpenApiGeneratrExtension)
    }

    private Configuration createConfiguration (Project project) {
        Configuration cfg = project.getConfigurations ().create ("openapiGeneratr")
        cfg.setVisible (false)
        cfg.setTransitive (true)
        cfg.setDescription ("the dependencies required for the openapi generatr plugin.")
        cfg
    }

    private addDependency (Project project, Configuration cfg) {
        def handler = project.getDependencies()

        cfg.withDependencies (new Action<DependencySet>() {
            @Override
            void execute(DependencySet dependencies) {
                [
                    handler.create("com.github.hauner.openapi:openapi-generatr-api:1.0.0.M3")
                ].each {
                    dependencies.add (it)
                }
            }
        })
    }

    private addRepository (Project project) {
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
                configureTasks (project)
            }

            private Map<String, Map> registerTasks (Project project) {
                extension.generatrs.get ().each { entry ->
                    project.tasks.register (
                        "generate${entry.key.capitalize ()}",
                        OpenApiGeneratrTask,
                        createTaskBuilderAction (entry.key, entry.value, extension))
                }
            }

            private void configureTasks (Project project) {
                def cfg = project.configurations.getByName ('openapiGeneratr')

                project.tasks.withType(OpenApiGeneratrTask).configureEach(new Action<OpenApiGeneratrTask>() {
                    @Override
                    void execute(OpenApiGeneratrTask task) {
                        task.configure {
                            task.dependencies = cfg
                        }
                    }
                })

            }

        }
    }

    /**
     * Provides an Action that configures 'generate{GeneratrName}' task from its configuration in
     * the OpenApiGeneratrExtension object.
     */
    private Action<OpenApiGeneratrTask> createTaskBuilderAction(
        String name, Map<String, ?> props, OpenApiGeneratrExtension extension) {

        new Action<OpenApiGeneratrTask>()  {
            public static final String TARGET_DIR = 'targetDir'

            @Override
            void execute (OpenApiGeneratrTask task) {
                task.setGeneratrName (name)
                task.setGeneratrProps (props)

                task.setGroup ('openapi generatr')
                task.setDescription ("generate sources from api with openapi-generatr-$name")

                task.setApiDir (getInputDirectory ())
                task.setTargetDir (getOutputDirectory ())

                copyApiPath (task)
            }

            private String getInputDirectory () {
                if (!extension.apiPath.present) {
                    return null
                }

                new File(extension.apiPath.get ()).parent
            }

            private String getOutputDirectory () {
                props.get (TARGET_DIR)
            }

            private copyApiPath (OpenApiGeneratrTask task) {
                // copy common api path to generatr props
                if (!props.containsKey ('apiPath')) {
                    if (!extension.apiPath.present) {
                        task.logger.warn ("'openapiGeneratr.apiPath' or 'openapiGeneratr.${name}.apiPath' not set!")
                        return
                    }

                    props.put ('apiPath', extension.apiPath.get ())
                }
            }

        }
    }

}
