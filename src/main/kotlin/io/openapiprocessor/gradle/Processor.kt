/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import groovy.lang.GString
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * represents an openapi-processor configured in [OpenApiProcessorExtension]
 */

open class Processor(configName: String): ProcessorBase() {

    companion object {
        const val API_PATH = "apiPath"
        const val TARGET_DIR = "targetDir"
    }

    var name: String
    val config: String
    val other: MutableMap<String, Any> = mutableMapOf()
    val dependencies: MutableCollection<Any> = mutableListOf()

    init {
        name = configName
        config = configName
    }

    fun processorName(processorName: String) {
        this.name = processorName
    }

    fun processorName(processorName: GString) {
        this.name = processorName.toString()
    }

    fun processor(dependency: Any) {
        dependencies.add(dependency)
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
     * allow to assign targetDir like
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

    fun prop(props: Map<String, Any>) {
        other.putAll(props)
    }

    fun prop(key: String, value: Any) {
        if (value is RegularFile) {
            other[key] = value.toString()
        } else {
            other[key] = value
        }
    }

    fun prop(key: GString, value: Any) {
        prop(key.toString(), value)
    }
}
