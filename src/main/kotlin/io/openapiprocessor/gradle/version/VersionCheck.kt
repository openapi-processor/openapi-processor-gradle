/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import org.gradle.api.logging.Logger
import org.slf4j.LoggerFactory
import java.io.File

class VersionCheck(private val rootDir: String, private val interval: String) {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name) as Logger

    fun canCheck(module: String): Boolean {
        when (interval) {
            "never" -> {
                return false
            }
            "always" -> {
                return true
            }
            "daily" -> {
                val settingsFile = File(rootDir, "build/openapiprocessor/${module}.yaml")
                val settings = VersionCheckSettings(settingsFile.toString())
                return VersionCheckLatest(settings, 1).shouldCheck(module)
            }
            else -> {
                log.warn("unknown checkUpdate interval '{}'", interval)
                return false
            }
        }
    }
}
