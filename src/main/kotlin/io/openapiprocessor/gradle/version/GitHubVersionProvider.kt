/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import groovy.json.JsonSlurper
import java.net.URI

import java.time.Instant

open class GitHubVersionProvider(private val versionUri: URI = LATEST) {

    @Suppress("UNCHECKED_CAST")
    open fun getVersion(): GitHubVersion {
        try {
            val json = JsonSlurper().parse(versionUri.toURL()) as Map<String, String>
            val name = json["name"]!!
            val text = json["body"]!!
            val publishedAt = Instant.parse(json["published_at"]!!)
            return GitHubVersion(name, publishedAt, text)
        } catch (t: Throwable) {
            throw GitHubVersionException(versionUri, t)
        }
    }

    companion object {
        val LATEST: URI = URI("https://api.github.com/repos/openapi-processor/openapi-processor-gradle/releases/latest")
    }
}
