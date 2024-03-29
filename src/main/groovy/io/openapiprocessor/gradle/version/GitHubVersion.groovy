/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import java.time.Instant

class GitHubVersion {
    String name
    Instant publishedAt
    String text
}
