/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.support

class Gradle {

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

    static List<String> VERSIONS_7 = [
        '7.2',
        /* '7.3', '7.3.1', '7.3.2', */ '7.3.3',
        /* '7.4', '7.4.1', */ '7.4.2',
        /* '7.5', */ '7.5.1',
        /* '7.6', '7.6.1', '7.6.2', '7.6.3', '7.6.4', '7.6.5' */ '7.6.6'
    ]

    // these only work with java 11
    static List<String> VERSIONS_7_JDK11 = [
        /* '7.0', '7.0.1', */ '7.0.2',
        /* '7.1', */ '7.1.1'
    ]
}
