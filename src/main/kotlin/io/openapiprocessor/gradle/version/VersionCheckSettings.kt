/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader
import java.io.FileWriter

import java.nio.file.Files
import java.time.Instant

class VersionCheckSettings(private val settingsName: String) {
    private val yaml = Yaml()
    private var settings: MutableMap<String, String> = LinkedHashMap<String, String>()

    fun get(name: String): Instant? {
        val at = settings[name] ?: return null
        return Instant.parse(at)
    }

    fun set(name: String) {
        settings[name] = Instant.now().toString()
    }

    fun set(name: String, at: Instant) {
        settings[name] = at.toString()
    }

    fun read() {
        val file = File(settingsName)
        when {
            file.exists() -> {
                settings = yaml.load(FileReader(file))
            }
            else -> {
                settings = LinkedHashMap()
            }
        }
    }

    fun write() {
        val file = File(settingsName)
        Files.createDirectories(file.parentFile.toPath())
        yaml.dump(settings, FileWriter(file))
    }
}
