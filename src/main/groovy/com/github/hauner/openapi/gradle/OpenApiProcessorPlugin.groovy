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
 * openapi-processor-gradle plugin.
 *
 * @author Martin Hauner
 */
class OpenApiProcessorPlugin implements Plugin<Project> {

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

        if (version < "5.5") {
            project.logger.error ("the current gradle version is ${version}")
            project.logger.error ("openapi-processor-gradle requires gradle 5.5+")
            return false
        }

        return true
    }

    private static OpenApiProcessorExtension createExtension (Project project) {
        project.extensions.create ('openapiProcessor', OpenApiProcessorExtension, project)
    }

    private addOpenApiGeneratrRepository (Project project) {
        project.repositories {
            mavenCentral()  // required?

            maven {
                url "https://dl.bintray.com/openapi-processor/primary"
                content {
                   includeGroup "io.openapiprocessor"
                }
                mavenContent {
                    releasesOnly()
                }
            }
            maven {
                url "https://oss.jfrog.org/artifactory/oss-snapshot-local"
                content {
                   includeGroup "io.openapiprocessor"
                }
                mavenContent {
                    snapshotsOnly()
                }
            }

            // obsolete
            maven {
                url "https://dl.bintray.com/hauner/openapi-processor"
                content {
                   includeGroupByRegex "com\\.github\\.hauner\\.openapi.*"
                }
            }
        }
    }

    /**
     * Provides an Action that create a 'process{ProcessorName}' task for each configured processor.
     */
    private Action<Project> createTasksBuilderAction (OpenApiProcessorExtension extension) {
        new Action<Project>() {

            @Override
            void execute (Project project) {
                registerTasks (project)
            }

            private void registerTasks (Project project) {
                extension.processors.get ().each { entry ->
                    def name = "process${entry.key.capitalize ()}"
                    def action = createTaskBuilderAction (entry.key, entry.value, extension)

                    project.tasks.register (name, OpenApiProcessorTask, action)
                }
            }
        }
    }

    /**
     * Creates an Action that configures a 'process{ProcessorName}' task from its configuration.
     */
    private Action<OpenApiProcessorTask> createTaskBuilderAction(
        String name, Processor config, OpenApiProcessorExtension extension) {

        new Action<OpenApiProcessorTask>()  {

            @Override
            void execute (OpenApiProcessorTask task) {
                task.setProcessorName (name)
                task.setProcessorProps (config.other)

                task.setGroup ('openapi processor')
                task.setDescription ("process openapi with openapi-processor-$name")

                copyApiPath (task)
                task.setApiDir (getInputDirectory ())
                task.setTargetDir (getOutputDirectory ())

                def project = task.getProject ()
                def handler = project.getDependencies ()
                Dependency api = handler.create("io.openapiprocessor:openapi-processor-api:1.1.0")

                if (!config.processorLib) {
                    task.logger.warn ("'openapiProcessor.${name}.processor' not set!")
                }

                Dependency dep = handler.create (config.processorLib)

                Configuration cfg = project.getConfigurations ().detachedConfiguration (api, dep)
                cfg.setVisible (false)
                cfg.setTransitive (true)
                cfg.setDescription ("the dependencies of the process${name.capitalize ()} task.")
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

            // copy common api path to openapi-processor props if not set
            private copyApiPath (OpenApiProcessorTask task) {
                if (!config.hasApiPath ()) {
                    if (!extension.apiPath.present) {
                        task.logger.warn ("'openapiProcessor.apiPath' or 'openapiProcessor.${name}.apiPath' not set!")
                        return
                    }

                    config.apiPath = extension.apiPath.get ()
                }
            }

        }
    }

}
