/*
 * Copyright 2013 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path
import java.time.Instant
import java.time.temporal.ChronoUnit

class VersionCheckSettingsSpec extends Specification {

    @TempDir
    Path testPath

    void "get last check without existing file is null"() {
        def file = testPath.resolve("openapiprocessor.yaml")
        def settings = new VersionCheckSettings(file.toString())

        when:
        settings.read()
        def time = settings.get("foo")

        then:
        time == null
    }

    void "get last check from existing file"() {
        def file = testPath.resolve("openapiprocessor.yaml")
        def settings = new VersionCheckSettings(file.toString())
        def at = Instant.now().minus(8, ChronoUnit.DAYS)
        settings.set("foo", at)
        settings.write()

        when:
        settings = new VersionCheckSettings(file.toString())
        settings.read()
        def time = settings.get("foo")

        then:
        time == at
    }

    void "set last check"() {
        def file = testPath.resolve("openapiprocessor.yaml")

        when:
        def settings = new VersionCheckSettings(file.toString())
        def at = Instant.now().minus(8, ChronoUnit.DAYS)
        settings.set("foo", at)
        settings.write()

        def last = new VersionCheckSettings(file.toString())
        last.read()
        def time = settings.get("foo")

        then:
        time == at
    }

}
