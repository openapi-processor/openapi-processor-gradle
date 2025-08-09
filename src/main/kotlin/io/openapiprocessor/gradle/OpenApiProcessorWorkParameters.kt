/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

interface OpenApiProcessorWorkParameters : WorkParameters {
    fun getProcessorName(): Property<String>
    fun getProcessorProps(): MapProperty<String, Any>
    fun getRootDir(): Property<String>
    fun getCheckUpdates(): Property<String>
}
