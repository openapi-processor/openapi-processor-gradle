/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.gradle

/**
 * backward compatibility, replaced by [io.openapiprocessor.gradle.ProcessorBase]
 */
@Deprecated("backward compatibility.")
class Processor(name: String): io.openapiprocessor.gradle.Processor(name)
