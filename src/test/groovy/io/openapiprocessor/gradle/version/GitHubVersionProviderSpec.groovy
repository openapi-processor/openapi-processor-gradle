/*
 * Copyright 2013 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import spock.lang.Specification

class GitHubVersionProviderSpec extends Specification {
    URI FIXED = new URI("https://api.github.com/repos/openapi-processor/openapi-processor-gradle/releases/tags/v2023.1")
    URI BAD = new URI("https://api.github.com/repos/openapi-processor/openapi-processor-gradle/releases/tags/bad")

    void "get github release version"() {
        def provider = new GitHubVersionProvider(FIXED)

        when:
        def version = provider.getVersion()

        then:
        version.name == "2023.1"
    }

    void "throws on error" () {
        def provider = new GitHubVersionProvider(BAD)

        when:
        provider.getVersion()

        then:
        thrown GitHubVersionException
    }
}
