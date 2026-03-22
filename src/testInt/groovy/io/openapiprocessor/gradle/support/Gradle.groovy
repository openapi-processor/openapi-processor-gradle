/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.support

class Gradle {
    static class Version {
        String version
        boolean groovy
        boolean kotlin
    }

    static private Version version(String version, boolean runsWithGroovy, boolean runsWithKotlin) {
        return new Version(version: version, groovy: runsWithGroovy, kotlin: runsWithKotlin)
    }

    static List<Version> VERSIONS_9 = [
        version('9.4.1', true, true),
        version('9.4.0', true, true),
        version('9.3.1', true, true),
        version('9.3.0', true, true),
        version('9.2.1', true, true),
        version('9.1.0', true, true),
        version('9.0.0', true, true)
    ]

    static List<Version> VERSIONS_8 = [
        version('8.14.4', true, true),
        version('8.14.3', true, true),
        version('8.13', true, true),
        version('8.12.1', true, true),
        version('8.11.1', true, true),
        version('8.10.2', true, true),
        version('8.9', true, true),
        version('8.8', true, true),
        version('8.7', true, true)
    ]
}
