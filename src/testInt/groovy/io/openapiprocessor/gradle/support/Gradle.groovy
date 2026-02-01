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
        version('8.7', true, true),
        version('8.6', true, true),
        version('8.5', true, true),
        version('8.4', true, true),
        version('8.3', true, true),
        version('8.2.1', true, true),
        // does not work with kotlin dsl since gradle 9.0.0 (?)
        version('8.1.1', true, false),
        version('8.0.2', true, false)
    ]

    static List<Version> VERSIONS_7 = [
        // does not work with kotlin dsl since gradle 9.0.0 (?)
        version('7.6.6', true, false),
        version('7.5.1', true, false),
        version('7.4.2', true, false),
        version('7.3.3', true, false),
        version('7.2', true, false),
        // jdk 11 only
        version('7.1.1', false, false),
        version('7.0.2', false, false)
    ]
}
