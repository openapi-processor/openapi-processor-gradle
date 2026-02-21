/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.lang.GString
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Extension object of the plugin. Used to configure the processors, e.g.
 * <pre>
 * openapiProcessor {
 *     ...
 *     apiPath "...."
 *
 *     spring {
 *       processor "...:...:..."
 *       targetDir "..."
 *
 *       ... other
 *     }
 *
 *     json {
 *       processor "...:...:..."
 *       targetDir "..."
 *
 *       ... other
 *     }
 *     ...
 *
 *     checkUpdates "never"|"daily"|"always"
 * }
 * </pre>
 */
abstract class OpenApiProcessorExtension(project: Project, objects: ObjectFactory): OpenApiProcessorExtensionBase() {

    /**
     * The path to the openapi YAML file. Used for all processors if not set in a nested processor
     * configuration.
     */
    val api: RegularFileProperty = project.objects.fileProperty()

    /**
     * Check automatically for updates. Can be "never"|"daily"|"always". Default is "never".
     */
    val checkUpdates: Property<String> = project.objects.property(String::class.java)

    /**
     * properties of the nested processor configurations by processor name, e.g.
     * <pre>
     *  openapiProcessor {
     *
     *      aProcessor {
     *          processor "...:...:..."
     *          targetDir "..."
     *
     *          // ... other properties
     *          prop "abc"
     *          prop "xyz"
     *      }
     *
     *  }
     * </pre>
     */
    val processors: NamedDomainObjectContainer<Processor> =
        objects.domainObjectContainer(Processor::class.java) { name ->
            objects.newInstance(Processor::class.java, name)
        }

    init {
        checkUpdates.set("never")
    }

    /**
     * groovy/kotlin dsl. create a new processor configuration.
     *
     * <pre>
     *  openapiProcessor {
     *    process("newProcessor") { ... }
     * }
     * </pre>
     *
     * @param name unique name of the processor
     * @param action [ProcessorBase] action
     * @return the new processor
     */
    fun process(name: String, action: Action<Processor>): Processor {
        val processor = processors.create(name)
        action.execute (processor)
        return processor
    }

    fun process(name: GString, action: Action<Processor>): Processor {
        return process (name.toString (), action)
    }

    /**
     * set apiPath.
     */
    fun apiPath(apiPath: String) {
        api.fileValue(File(apiPath))
    }

    fun apiPath(apiPath: File) {
        api.fileValue(apiPath)
    }

    fun apiPath(apiPath: RegularFile) {
        api.value(apiPath)
    }

    fun apiPath(apiPath: Provider<RegularFile>) {
        api.value(apiPath.get())
    }

    /**
     * set apiPath.
     */
    fun apiPath(apiPath: GString) {
        apiPath(apiPath.toString())
    }

    fun setApiPath(apiPath: File) {
        api.set(apiPath)
    }

    fun setApiPath(apiPath: RegularFile) {
        api.set(apiPath)
    }

    fun checkUpdates(check: String) {
        checkUpdates.set(check)
    }

    // declaration clash
//    fun getCheckUpdates(): Property<String> {
//        return checkUpdates
//    }
}
