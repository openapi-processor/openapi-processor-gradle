/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class VersionCheckSpec extends Specification {

    @TempDir
    Path testPath

    void "should not check if interval is 'never'"() {
        def version = new VersionCheck(testPath.toString(), "never")

        when:
        def canCheck = version.canCheck("gradle")

        then:
        !canCheck
    }

    void "should check if interval is 'always'"() {
        def version = new VersionCheck(testPath.toString(), "always")

        when:
        def canCheck = version.canCheck("gradle")

        then:
        canCheck
    }

    void "should check if interval is 'daily'"() {
        def version = new VersionCheck(testPath.toString(), "daily")

        when:
        def canCheck = version.canCheck("gradle")

        then:
        canCheck
    }
}
