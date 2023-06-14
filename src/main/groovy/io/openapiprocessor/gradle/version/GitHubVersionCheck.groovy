/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version


import org.gradle.api.logging.Logger
import org.slf4j.LoggerFactory

class GitHubVersionCheck {
    private Logger log = LoggerFactory.getLogger(GitHubVersionCheck) as Logger

    private GitHubVersionProvider provider
    private String currentVersion

    GitHubVersionCheck(GitHubVersionProvider provider, String currentVersion) {
        this.provider = provider
        this.currentVersion = currentVersion
    }

    boolean check () {
        try {
            def version = provider.getVersion()

            if (version.name > currentVersion && !version.name.contains("SNAPSHOT")) {
                log.quiet("openapi-processor-gradle version ${version.name} is available! I'm version ${currentVersion}.")
                return true
            }
            return false

        } catch (GitHubVersionException ignore) {
            // just ignore, do not complain
            return false
        }
    }
}
