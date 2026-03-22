/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.Companion.EXTENSION_NAME_DEFAULT
import io.openapiprocessor.gradle.OpenApiProcessorExtensionUtils.Companion.createExtension
import io.openapiprocessor.gradle.version.GitHubVersionCheck
import io.openapiprocessor.gradle.version.GitHubVersionProvider
import io.openapiprocessor.gradle.version.VersionCheck
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.util.GradleVersion
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

        val extension = createExtension(project)
        createProcessingTasks(project, extension)

        checkUpdates(project, extension)
    }

    private fun addOpenApiProcessorRepository (project: Project) {
        val snapshots = project.findProperty ("openapi-processor-gradle.snapshots")
        if ((snapshots == null) || (snapshots != "true")) {
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
     * Check for plugin updates.
     */
    private fun checkUpdates(project: Project, extension: OpenApiProcessorExtension) {
        val interval = extension.checkUpdates.get()

        val version = VersionCheck(project.rootDir.absolutePath, interval)
        if (!version.canCheck("gradle"))
            return

        checkLatestRelease()
    }

    /**
     * Create a 'process{ProcessorName}' task for each configured processor.
     */
    @Suppress("UnstableApiUsage")
    fun createProcessingTasks(project: Project, extension: OpenApiProcessorExtension) {
        extension.processors.configureEach(
            object : Action<Processor> {
                override fun execute(processor: Processor) {
                    val name = "process${processor.name.replaceFirstChar { it.uppercase() }}"

                    val scope = project.configurations.dependencyScope("${processor.name}Scope") {
                        fromDependencyCollector(processor.dependencies.process)
                    }

                    project.dependencies.add(
                        scope.name,
                        "io.openapiprocessor:openapi-processor-api:${Versions.api}")

                    val resolvable = project.configurations.resolvable("${processor.name}Classpath") {
                            extendsFrom(scope.get())
                            isCanBeResolved = true
                            isCanBeConsumed = false
                        }

                    project.tasks.register(
                        name,
                        OpenApiProcessorTask::class.java,
                        createTaskBuilderAction(processor, extension, resolvable)
                    )
                }
            })
    }

    /**
     * Creates an Action that configures a 'process{ProcessorName}' task from its configuration.
     */
    @Suppress("UnstableApiUsage")
    private fun createTaskBuilderAction(
        processor: Processor,
        extension: OpenApiProcessorExtension,
        processorClasspath: NamedDomainObjectProvider<ResolvableConfiguration>
    ): Action<OpenApiProcessorTask> {

        return object: Action<OpenApiProcessorTask> {
            // copy common api path to openapi-processor props if not set
            fun copyApiPath (task: OpenApiProcessorTask) {
                if(processor.hasApiPath ())
                    return

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

                task.getRootDir().set(project.rootDir.absolutePath)
                task.getCheckUpdates().set(extension.checkUpdates)
                task.getLogClasspath().set(extension.logClasspath)
                task.getProcessorName().set(processor.name)
                task.getProcessorProps().set(processor.other)

                task.group = "openapi processor"
                task.description = "process openapi with openapi-processor-${processor.name}"

                copyApiPath(task)
                task.getApiDir().set(project.layout.projectDirectory.dir(getInputDirectory()))
                task.getTargetDir().set(project.layout.projectDirectory.dir(getOutputDirectory()))
                processor.getMapping()?.let { task.getMapping().set(project.file(it)) }

                task.getDependencies().from(processorClasspath)
            }
        }
    }

    private fun checkLatestRelease(): Boolean {
        return GitHubVersionCheck(GitHubVersionProvider(), Versions.version).check()
    }

    companion object {
        private fun isSupportedGradleVersion(project: Project): Boolean {
            val currentVersion = GradleVersion.current()
            val minVersion = GradleVersion.version("8.7")

            if (currentVersion < minVersion) {
                project.logger.error ("the current gradle version is $currentVersion")
                project.logger.error ("openapi-processor-gradle requires gradle $minVersion+")
                return false
            }

            return true
        }
    }
}
