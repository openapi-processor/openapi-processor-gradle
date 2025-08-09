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

class VersionCheckLatestSpec extends Specification {

    @TempDir
    Path testPath

    void "should check if there was no latest check"() {
        def file = testPath.resolve(".openapiprocessor.yaml")
        def settings = new VersionCheckSettings(file.toString())
        def latest = new VersionCheckLatest(settings, 1, new VersionCheckLatest.DefaultTime())

        when:
        def check = latest.shouldCheck("gradle")

        then:
        check
    }

    void "should check if last check is too old"() {
        def now = Instant.now()

        Time time = Mock()
        time.now() >> now

        def file = testPath.resolve(".openapiprocessor.yaml")
        def settings = new VersionCheckSettings(file.toString())
        settings.set("gradle", Instant.now().minus(2, ChronoUnit.DAYS))

        def latest = new VersionCheckLatest(settings, 1, time)

        when:
        def check = latest.shouldCheck("gradle")

        then:
        check
    }

    void "should NOT check if last check is too young"() {
        def now = Instant.now()

        Time time = Mock()
        time.now() >> now

        def file = testPath.resolve(".openapiprocessor.yaml")
        def settings = new VersionCheckSettings(file.toString())
        settings.set("gradle", Instant.now().minus(0, ChronoUnit.DAYS))

        def latest = new VersionCheckLatest(settings, 1, time)

        when:
        def check = latest.shouldCheck("gradle")

        then:
        !check
    }
}
