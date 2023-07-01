/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import io.openapiprocessor.gradle.Time

import java.time.Instant
import java.time.temporal.ChronoUnit

class VersionCheckLatest {
    private VersionCheckSettings settings
    private int interval;
    private Time time

    static class DefaultTime implements Time {
        @Override
        Instant now() {
            return Instant.now()
        }
    }

    VersionCheckLatest(VersionCheckSettings settings, int intervalDays, Time time = new DefaultTime()) {
        this.settings = settings
        this.interval = intervalDays
        this.time = time
    }

    boolean shouldCheck(String module) {
        def now = time.now()
        def last = settings.get(module)
        if (last == null) {
            settings.set(module, now)
            return true
        }

        def shouldCheck = last.isBefore(now.minus(interval, ChronoUnit.DAYS))
        if (shouldCheck) {
            settings.set(module, now)
            return true
        }

        return false
    }
}
