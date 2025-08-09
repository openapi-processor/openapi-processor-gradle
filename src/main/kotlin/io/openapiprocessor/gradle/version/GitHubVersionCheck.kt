/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version


import org.gradle.api.logging.Logger
import org.slf4j.LoggerFactory

class GitHubVersionCheck(private val provider: GitHubVersionProvider, private val currentVersion: String) {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name) as Logger

    fun check(): Boolean {
        try {
            val version = provider.getVersion()

            if (version.name > currentVersion && !version.name.contains("SNAPSHOT")) {
                log.quiet("openapi-processor-gradle version ${version.name} is available! I'm version ${currentVersion}.")
                return true
            }
            return false

        } catch (ignore: GitHubVersionException) {
            // just ignore, do not complain
            return false
        }
    }
}
