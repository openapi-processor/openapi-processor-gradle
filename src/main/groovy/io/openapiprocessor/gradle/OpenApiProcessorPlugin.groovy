/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import io.openapiprocessor.gradle.version.GitHubVersionCheck
import io.openapiprocessor.gradle.version.GitHubVersionProvider
import io.openapiprocessor.gradle.version.VersionCheck
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

import static io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.*

/**
 * openapi-processor-gradle plugin.
 */
class OpenApiProcessorPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {
        if (!isSupportedGradleVersion (project)) {
            return
        }

        addOpenApiProcessorRepository (project)

        createExtension (project)

        project.afterEvaluate (createCheckUpdatesAction ())
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

    private void addOpenApiProcessorRepository (Project project) {
        def snapshots = project.findProperty ("openapi-processor-gradle.snapshots")
        if (snapshots == null || snapshots != "true") {
            project.logger.debug("openapi-processor: snapshot repository disabled")
            return
        }

        project.repositories {
            maven {
                url "https://oss.sonatype.org/content/repositories/snapshots"
                mavenContent {
                    snapshotsOnly ()
                }
            }
        }
        project.logger.debug("openapi-processor: snapshot repository enabled")
    }

    /**
     * Provides an Action that checks for plugin updates.
     */
    private Action<Project> createCheckUpdatesAction () {
        return new Action<Project>() {
            @Override
            void execute (Project project) {
                def extension = getExtension (project)
                def interval = extension.checkUpdates.get()

                def version = new VersionCheck(project.rootDir.absolutePath, interval)
                if (!version.canCheck("gradle"))
                    return

                checkLatestRelease ()
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
                def extension = getExtension (project)
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
                def project = task.getProject ()

                task.processorName.set (processor.name)
                task.processorProps.set (processor.other)

                task.setGroup ('openapi processor')
                task.setDescription ("process openapi with openapi-processor-${processor.name}")

                copyApiPath (task)
                task.apiDir.set (inputDirectory)
                task.targetDir.set (outputDirectory)

                processor.getOther().get ("mapping")?.with { mapping ->
                    task.inputFiles.value (Set.of (mapping))
                }

                def handler = project.getDependencies ()
                List<Dependency> dependencies = []

                if (processor.dependencies.empty) {
                    task.logger.warn ("'${EXTENSION_NAME_DEFAULT}.${name}.processor' not set!")
                }

                dependencies.add (handler.create("io.openapiprocessor:openapi-processor-api:${Version.api}"))

                processor.dependencies.each {
                    dependencies.add (handler.create (it))
                }

                Dependency[] deps = dependencies.toArray (new Dependency[0])

                Configuration cfg = project.getConfigurations ()
                    .detachedConfiguration (deps)

                cfg.setVisible (false)
                cfg.setTransitive (true)
                cfg.setDescription ("the dependencies of the process${name.capitalize ()} task.")
                task.dependencies.from (cfg)
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

                def extension = getExtension (task.project)
                if (!extension.api.present) {
                    task.logger.warn ("'${extName}.apiPath'!")
                    return
                }

                processor.apiPath = extension.api.get ()
            }
        }
    }

    private void checkLatestRelease () {
        new GitHubVersionCheck(new GitHubVersionProvider(), Version.version).check()
    }
}
