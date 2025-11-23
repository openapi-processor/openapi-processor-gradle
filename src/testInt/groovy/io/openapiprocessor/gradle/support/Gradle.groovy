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

    static List<String> VERSIONS_9 = [
        '9.0.0',
        '9.1.0',
        /* '9.2.0', */ '9.2.1'
    ]

    static List<String> VERSIONS_8 = [
        /* '8.0', '8.0.1', */ '8.0.2',
        /* '8.1', */ '8.1.1',
        /* '8.2', */ '8.2.1',
        '8.3',
        '8.4',
        '8.5',
        '8.6',
        '8.7',
        '8.8',
        '8.9',
        /* '8.10', '8.10.1', */ '8.10.2',
        /* '8.11', */ '8.11.1',
        /* '8.12', */ '8.12.1',
        '8.13',
        /* '8.14', '8.14.1', '8.14.2' */ '8.14.3'
    ]

    static List<Version> VERSIONS_7 = [
        // don't run since 9.2.1 with kotlin
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
