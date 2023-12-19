/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle


import io.openapiprocessor.gradle.support.PluginSpec

class SnapshotsRepositorySpec extends PluginSpec {

    @Override
    String getBuildFileName () {
        'build.gradle'
    }

    @Override
    String getBuildFile (String projectDir) {
        """\
        plugins {
            id("io.openapiprocessor.openapi-processor")
        }
        """.stripIndent ()
    }

    @Override
    List<String> getGradleArguments () {
        ['--stacktrace', '--debug']
    }

    void "does not add snapshot repository if ot enabled or disabled" () {
        when:
        def result = build("8.5", """\
            """.stripIndent())

        then:
        result.output.contains("snapshot repository disabled")
    }

    void "does not add snapshot repository if disabled" () {
        when:
        def result = build("8.5", """\
            openapi-processor-gradle.snapshots = false
            """.stripIndent())

        then:
        result.output.contains("snapshot repository disabled")
    }

    void "adds snapshot repository" () {
        when:
        def result = build("8.5", """\
            openapi-processor-gradle.snapshots = true
            """.stripIndent())

        then:
        result.output.contains("snapshot repository enabled")
    }
}
