/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.gradle

import org.gradle.api.model.ObjectFactory

/**
 * backward compatibility, replaced by [io.openapiprocessor.gradle.ProcessorBase]
 */
@Deprecated("backward compatibility.")
class Processor(name: String, objects: ObjectFactory): io.openapiprocessor.gradle.Processor(name, objects)
