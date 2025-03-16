/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Project

class OpenApiProcessorExtensionUtils {
    static final String EXTENSION_NAME_DEFAULT = 'openapiProcessor'
    static final String EXTENSION_NAME_ALTERNATIVE = 'openapi'

    static void createExtension (Project project) {
        def extension = project.extensions.create (
                EXTENSION_NAME_DEFAULT,
                OpenApiProcessorExtension,
                project)

        // make same extension object available under alternative name
        project.extensions.add(EXTENSION_NAME_ALTERNATIVE, extension)
    }

    static OpenApiProcessorExtension getExtension(Project project) {
        return project.extensions.findByName (EXTENSION_NAME_DEFAULT) as OpenApiProcessorExtension
    }
}
