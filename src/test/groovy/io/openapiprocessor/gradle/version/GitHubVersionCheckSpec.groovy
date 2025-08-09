/*
 * Copyright 2013 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import spock.lang.Specification
import java.time.Instant

class GitHubVersionCheckSpec extends Specification {

    void "finds newer version" () {
        def provider = Stub GitHubVersionProvider
        provider.getVersion() >> new GitHubVersion("2023.2", Instant.now(), "newer")

        when:
        def check = new GitHubVersionCheck(provider, "2023.1")
        def foundNewerVersion = check.check()

        then:
        foundNewerVersion
    }

    void "ignores older or equal version" () {
        def provider = Stub GitHubVersionProvider
        provider.getVersion() >>> [
                new GitHubVersion("2022.3", Instant.now(), "newer"),
                new GitHubVersion("2022.2", Instant.now(), "newer")
        ]

        when:
        def check = new GitHubVersionCheck(provider, "2023.1")
        def foundNewerVersion1 = check.check()
        def foundNewerVersion2 = check.check()

        then:
        !foundNewerVersion1
        !foundNewerVersion2
    }

    void "ignores newer snapshot version" () {
        def provider = Stub GitHubVersionProvider
        provider.getVersion() >> new GitHubVersion("2023.2-SNAPSHOT", Instant.now(), "snapshot")

        when:
        def check = new GitHubVersionCheck(provider, "2023.1")
        def foundNewerVersion = check.check()

        then:
        !foundNewerVersion
    }

    void "ignores error"() {
        def provider = Stub GitHubVersionProvider
        provider.getVersion() >> { throw new GitHubVersionException(new URI("/bad"), new Exception()) }

        when:
        def check = new GitHubVersionCheck(provider, "any")
        check.check()

        then:
        notThrown(GitHubVersionException)
    }
}
