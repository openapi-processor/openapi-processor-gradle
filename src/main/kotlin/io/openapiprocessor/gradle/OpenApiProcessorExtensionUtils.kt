/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Project

class OpenApiProcessorExtensionUtils {
    companion object {
        const val EXTENSION_NAME_DEFAULT = "openapiProcessor"
        const val EXTENSION_NAME_ALTERNATIVE = "openapi"

        fun createExtension (project: Project) {
            val extension = project.extensions.create (
                    EXTENSION_NAME_DEFAULT,
                    OpenApiProcessorExtension::class.java,
                    project)

            // make same extension object available under alternative name
            project.extensions.add(EXTENSION_NAME_ALTERNATIVE, extension)
        }

        fun getExtension(project: Project): OpenApiProcessorExtension {
            return project.extensions.getByName (EXTENSION_NAME_DEFAULT) as OpenApiProcessorExtension
        }
    }
}
