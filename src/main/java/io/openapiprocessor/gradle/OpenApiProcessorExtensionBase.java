/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle;

import org.gradle.api.file.RegularFileProperty;

abstract class OpenApiProcessorExtensionBase {
    /**
     * the path to the openapi yaml file. Used for all processors if not set in a nested processor
     * configuration.
     */
    RegularFileProperty apiPath;
}
