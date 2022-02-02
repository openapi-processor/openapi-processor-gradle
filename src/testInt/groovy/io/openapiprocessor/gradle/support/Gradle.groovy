/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.support

class Gradle {

    static List<String> VERSIONS_7 = [
        '7.0', '7.0.1', '7.0.2',
        '7.1', '7.1.1',
        '7.2',
        '7.3', '7.3.1', '7.3.2', '7.3.3'
    ]

    static List<String> VERSIONS_6 = [
        '6.0', '6.0.1',
        '6.1', '6.1.1',
        '6.2', '6.2.1', '6.2.2',
        '6.3',
        '6.4', '6.4.1',
        '6.5', '6.5.1',
        '6.6', '6.6.1',
        '6.7', '6.7.1',
        '6.8', '6.8.1', '6.8.2', '6.8.3',
        '6.9', '6.9.1'
    ]

    static List<String> VERSIONS_6_KOTLIN = [
        '6.5',
        '6.5.1',
        '6.6', '6.6.1',
        '6.7', '6.7.1',
        '6.8', '6.8.1', '6.8.2', '6.8.3',
        '6.9', '6.9.1'
    ]

    // 5.5 is the minimum required version to run the tests
    static List<String> VERSIONS_5 = [
        '5.5', '5.5.1',
        '5.6', '5.6.1', '5.6.2', '5.6.3', '5.6.4'
    ]

}
