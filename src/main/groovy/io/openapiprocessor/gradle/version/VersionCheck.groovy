/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import org.gradle.api.logging.Logger
import org.slf4j.LoggerFactory

class VersionCheck {
    private final Logger log = (Logger) LoggerFactory.getLogger(VersionCheck.class);

    private String rootDir
    private String interval

    VersionCheck(String rootDir, String interval) {
        this.rootDir = rootDir
        this.interval = interval
    }

    boolean canCheck(String module) {
        if (interval == "never") {
            return false
        } else if (interval == "always") {
            return true
        } else if (interval == "daily") {
            def settingsFile = new File(rootDir, "build/openapiprocessor/${module}.yaml")
            def settings = new VersionCheckSettings(settingsFile.toString())
            new VersionCheckLatest(settings, 1).shouldCheck(module)
        } else {
            log.warn("unknown checkUpdate interval '{}'", interval)
        }
    }
}
