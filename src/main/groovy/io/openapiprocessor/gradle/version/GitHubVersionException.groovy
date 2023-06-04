/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

class GitHubVersionException extends RuntimeException {

    GitHubVersionException(URI uri, Throwable cause) {
        super("can't find version: ${uri}!", cause)
    }

}
