/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import java.net.URI

class GitHubVersionException(uri: URI, cause: Throwable)
    : RuntimeException("can't find version: ${uri}!", cause)
