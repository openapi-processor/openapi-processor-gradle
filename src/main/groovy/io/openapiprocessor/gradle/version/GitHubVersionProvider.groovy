/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import groovy.json.JsonSlurper

import java.time.Instant

class GitHubVersionProvider {
    public static final URI LATEST = new URI("https://api.github.com/repos/openapi-processor/openapi-processor-gradle/releases/latest")

    private URI versionUri

    GitHubVersionProvider() {
        versionUri = LATEST
    }

    GitHubVersionProvider(URI versionUri) {
        this.versionUri = versionUri
    }

    GitHubVersion getVersion () {
        try {
            def json = new JsonSlurper().parse(versionUri.toURL()) as Map
            return new GitHubVersion(
                    name: json.name,
                    publishedAt: Instant.parse(json.published_at as CharSequence),
                    text: json.body
            )
        } catch (Throwable t) {
            throw new GitHubVersionException(versionUri, t)
        }
    }
}
