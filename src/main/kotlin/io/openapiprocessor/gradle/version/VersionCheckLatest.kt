/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import java.time.Instant
import java.time.temporal.ChronoUnit

class VersionCheckLatest(
    private val settings: VersionCheckSettings,
    private val intervalDays: Long,
    private val time: Time = DefaultTime(),
) {
    class DefaultTime: Time {
        override fun now(): Instant {
            return Instant.now()
        }
    }

    fun shouldCheck(module: String): Boolean {
        val now = time.now()
        val last = settings.get(module)
        if (last == null) {
            settings.set(module, now)
            return true
        }

        val shouldCheck = last.isBefore(now.minus(intervalDays, ChronoUnit.DAYS))
        if (shouldCheck) {
            settings.set(module, now)
            return true
        }

        return false
    }
}
