/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

/**
 * openapi-processor-gradle plugin.
 */
class OpenApiProcessorPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = 'openapiProcessor'

    @Override
    void apply (Project project) {
        if (!isSupportedGradleVersion (project)) {
            return
        }

        addOpenApiProcessorRepository (project)

        def ext = createExtension (project)
        project.afterEvaluate (createTasksBuilderAction ())
    }

    private static boolean isSupportedGradleVersion (Project project) {
        String version = project.gradle.gradleVersion

        if (version < "7.0") {
            project.logger.error ("the current gradle version is ${version}")
            project.logger.error ("openapi-processor-gradle requires gradle 7.0+")
            return false
        }

        return true
    }

    private static OpenApiProcessorExtension createExtension (Project project) {
        project.extensions.create (EXTENSION_NAME, OpenApiProcessorExtension, project)
    }

    private addOpenApiProcessorRepository (Project project) {
        project.repositories {
            mavenCentral()

            maven {
                url "https://oss.sonatype.org/content/repositories/snapshots"
                mavenContent {
                    snapshotsOnly()
                }
            }

            // obsolete
            maven {
                url "https://dl.bintray.com/openapi-processor/primary"
                content {
                   includeGroup "io.openapiprocessor"
                }
                mavenContent {
                    releasesOnly()
                }
            }

            // obsolete
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
    private Action<Project> createTasksBuilderAction () {
        return new Action<Project>() {
            @Override
            void execute (Project project) {
                OpenApiProcessorExtension extension = project.extensions.findByName (EXTENSION_NAME)
                extension.processors.get ().each { entry ->
                    def name = "process${entry.key.capitalize ()}"
                    def action = createTaskBuilderAction (entry.key, entry.value)
                    project.tasks.register (name, OpenApiProcessorTask, action)
                }
            }
        }
    }

    /**
     * Creates an Action that configures a 'process{ProcessorName}' task from its configuration.
     */
    private Action<OpenApiProcessorTask> createTaskBuilderAction(String name, Processor processor) {
        new Action<OpenApiProcessorTask>()  {
            @Override
            void execute (OpenApiProcessorTask task) {
                task.setProcessorName (name)
                task.setProcessorProps (processor.other)

                task.setGroup ('openapi processor')
                task.setDescription ("process openapi with openapi-processor-$name")

                copyApiPath (task)
                task.setApiDir (getInputDirectory ())
                task.setTargetDir (getOutputDirectory ())

                def project = task.getProject ()
                def handler = project.getDependencies ()
                List<Dependency> dependencies = []

                if (processor.dependencies.empty) {
                    task.logger.warn ("'$EXTENSION_NAME.$name.processor' not set!")
                }

                dependencies.add (handler.create(
                    "io.openapiprocessor:openapi-processor-api:${Version.api}"))

                processor.dependencies.each {
                    dependencies.add (handler.create (it))
                }

                Dependency[] deps = dependencies.toArray (new Dependency[0])

                Configuration cfg = project.getConfigurations ()
                    .detachedConfiguration (deps)

                cfg.setVisible (false)
                cfg.setTransitive (true)
                cfg.setDescription ("the dependencies of the process${name.capitalize ()} task.")
                task.dependencies = cfg
            }

            private String getInputDirectory () {
                String path = processor.apiPath
                def file = new File (path)
                file.parent
            }

            private String getOutputDirectory () {
                processor.targetDir
            }

            // copy common api path to openapi-processor props if not set
            private copyApiPath (OpenApiProcessorTask task) {
                if(processor.hasApiPath ())
                    return

                def extension = task.project.extensions.findByName (EXTENSION_NAME)
                if (!extension.apiPath.present) {
                    task.logger.warn ("'$EXTENSION_NAME.apiPath' or '$EXTENSION_NAME.$name.apiPath' not set!")
                    return
                }

                processor.apiPath = extension.apiPath.get ()
            }
        }
    }

}
