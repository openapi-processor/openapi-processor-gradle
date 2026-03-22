/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.lang.GString
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested
import java.io.File
import javax.inject.Inject

/**
 * represents an openapi-processor configured in [OpenApiProcessorExtension]
 */

open class Processor @Inject constructor(configName: String, val objects: ObjectFactory): ProcessorBase() {

    companion object {
        const val API_PATH = "apiPath"
        const val TARGET_DIR = "targetDir"
    }

    interface ProcessorDependencies {
        val process: DependencyCollector
    }

    var name: String
    val config: String
    val other: MutableMap<String, Any> = mutableMapOf()

    init {
        name = configName
        config = configName
    }

    @get:Nested
    val dependencies: ProcessorDependencies = objects.newInstance(ProcessorDependencies::class.java)

    fun dependencies(action: Action<in ProcessorDependencies>) {
        action.execute(dependencies)
    }

    fun processorName(processorName: String) {
        this.name = processorName
    }

    fun processorName(processorName: GString) {
        this.name = processorName.toString()
    }

    //@Deprecated("use dependencies { process(dependency) } instead")
    fun processor(dependency: Any) {
        @Suppress("UNCHECKED_CAST")
        when (dependency) {
            is CharSequence -> dependencies.process.add(dependency)
            is File -> dependencies.process.add(objects.fileCollection().from(dependency))
            is FileCollection -> dependencies.process.add(dependency)
            is Dependency -> dependencies.process.add(dependency)
            is Provider<*> -> dependencies.process.add(dependency as Provider<out Dependency>)
            else -> dependencies.process.add(dependency.toString())
        }
    }

    fun getTargetDir(): String {
        return other[TARGET_DIR] as String
    }

    fun targetDir(targetDir: String) {
        other[TARGET_DIR] = targetDir
    }

    fun targetDir(targetDir: GString) {
        other[TARGET_DIR] = targetDir.toString()
    }

    fun targetDir(targetDir: Directory) {
        other[TARGET_DIR] = targetDir.toString()
    }

    fun targetDir(targetDir: Provider<Directory>) {
        other[TARGET_DIR] = targetDir.get().toString()
    }

    fun setTargetDir(targetDir: String) {
        other[TARGET_DIR] = targetDir
    }

    fun setTargetDir(targetDir: GString) {
        other[TARGET_DIR] = targetDir.toString ()
    }

    fun setTargetDir(targetDir: Directory) {
        other[TARGET_DIR] = targetDir.toString()
    }

    /**
     * allow assigning targetDir like
     *
     * {@code targetDir = layout.buildDirectory.dir("openapi")}
     *
     * @param targetDir targetDir provider
     */
    fun setTargetDir(targetDir: Provider<Directory>) {
        other[TARGET_DIR] = targetDir.get().toString()
    }

    fun apiPath(apiPath: String) {
        other[API_PATH] = apiPath
    }

    fun apiPath(apiPath: GString) {
        other[API_PATH] = apiPath.toString ()
    }

    fun hasApiPath(): Boolean {
        return other.containsKey(API_PATH)
    }

    fun getApiPath(): String {
        return other.get (API_PATH) as String
    }

    fun setApiPath(path: String) {
        other[API_PATH] = path
    }

    fun setApiPath(apiPath: RegularFile) {
        other[API_PATH] = apiPath.toString()
    }

    fun getMapping(): String? {
        return other["mapping"]?.toString()
    }

    fun prop(props: Map<String, Any>) {
        other.putAll(props)
    }

    fun prop(key: String, value: Any) {
        when (value) {
            is RegularFile -> {
                other[key] = value.toString()
            }
            is Provider<*> -> {
                prop(key, value.get())
            }
            else -> {
                other[key] = value
            }
        }
    }

    fun prop(key: GString, value: Any) {
        prop(key.toString(), value)
    }
}
