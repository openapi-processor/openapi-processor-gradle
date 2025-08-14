/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.Companion.EXTENSION_NAME_DEFAULT
import io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.Companion.createExtension
import io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.Companion.getExtension
import io.openapiprocessor.gradle.version.GitHubVersionCheck
import io.openapiprocessor.gradle.version.GitHubVersionProvider
import io.openapiprocessor.gradle.version.VersionCheck
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import java.io.File

/**
 * openapi-processor-gradle plugin.
 */
class OpenApiProcessorPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        if (!isSupportedGradleVersion(project)) {
            return
        }

        addOpenApiProcessorRepository(project)

        createExtension(project)

        project.afterEvaluate(createCheckUpdatesAction())
        project.afterEvaluate(createTasksBuilderAction())
    }

    private fun addOpenApiProcessorRepository (project: Project) {
        val snapshots = project.findProperty ("openapi-processor-gradle.snapshots")
        if (snapshots == null || snapshots != "true") {
            project.logger.debug("openapi-processor: snapshot repository disabled")
            return
        }

        project.repositories.maven {
            url = project.uri("https://oss.sonatype.org/content/repositories/snapshots/")
            mavenContent { snapshotsOnly() }
        }

        project.logger.debug("openapi-processor: snapshot repository enabled")
    }

    /**
     * Provides an Action that checks for plugin updates.
     */
    private fun createCheckUpdatesAction(): Action<Project> {
        return object : Action<Project> {
            override fun execute(project: Project) {
                val extension = getExtension(project)
                val interval = extension.checkUpdates.get()

                val version = VersionCheck(project.rootDir.absolutePath, interval)
                if (!version.canCheck("gradle"))
                    return

                checkLatestRelease()
            }
        }
    }

    /**
     * Provides an Action that create a 'process{ProcessorName}' task for each configured processor.
     */
    private fun createTasksBuilderAction(): Action<Project> {
        return object : Action<Project> {
            override fun execute(project: Project) {
                val extension = getExtension(project)
                extension.processors.get().forEach { entry ->
                    val name = "process${entry.key.capitalize()}"
                    val action = createTaskBuilderAction(entry.key, entry.value)
                    project.tasks.register(name, OpenApiProcessorTask::class.java, action)
                }
            }
        }
    }

    /**
     * Creates an Action that configures a 'process{ProcessorName}' task from its configuration.
     */
    private fun createTaskBuilderAction(name: String, processor: Processor): Action<OpenApiProcessorTask> {
        return object: Action<OpenApiProcessorTask> {
            // copy common api path to openapi-processor props if not set
            fun copyApiPath (task: OpenApiProcessorTask) {
                if(processor.hasApiPath ())
                    return

                val extension = getExtension(task.project)
                if (!extension.api.isPresent) {
                    task.logger.warn ("'${EXTENSION_NAME_DEFAULT}.apiPath'!")
                    return
                }

                processor.setApiPath(extension.api.get())
            }

            fun getInputDirectory(): String {
                val path = processor.getApiPath()
                val file = File(path)
                return file.parent
            }

            fun getOutputDirectory(): String {
                return processor.getTargetDir()
            }

            override fun execute(task: OpenApiProcessorTask) {
                val project = task.project

                task.getProcessorName().set (processor.name)
                task.getProcessorProps().set(processor.other)
                task.group = "openapi processor"
                task.description = "process openapi with openapi-processor-${processor.name}"

                copyApiPath (task)
                task.getApiDir().set(getInputDirectory())
                task.getTargetDir().set(getOutputDirectory())

                val handler = project.dependencies
                val dependencies = ArrayList<Dependency>()

                if (processor.dependencies.isEmpty()) {
                    task.logger.warn ("'${EXTENSION_NAME_DEFAULT}.${name}.processor' not set!")
                }

                dependencies.add (handler.create("io.openapiprocessor:openapi-processor-api:${Versions.api}"))

                processor.dependencies.forEach {
                    dependencies.add (handler.create (it))
                }

                val deps = dependencies.toTypedArray()
                val cfg = project.configurations.detachedConfiguration(*deps)

                cfg.isVisible = false
                cfg.isTransitive = true
                cfg.description = "the dependencies of the process${name.capitalize ()} task."
                task.getDependencies().from (cfg)
            }
        }
    }

    private fun checkLatestRelease(): Boolean {
        return GitHubVersionCheck(GitHubVersionProvider(), Versions.version).check()
    }

    companion object {
        private fun isSupportedGradleVersion(project: Project): Boolean {
            val version: String = project.gradle.gradleVersion

            if (version < "7.0") {
                project.logger.error ("the current gradle version is $version")
                project.logger.error ("openapi-processor-gradle requires gradle 7.0+")
                return false
            }

            return true
        }
    }
}
