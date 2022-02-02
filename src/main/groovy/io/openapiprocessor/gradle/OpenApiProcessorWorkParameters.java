/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

interface OpenApiProcessorWorkParameters extends WorkParameters {

    Property<String> getProcessorName ();

    MapProperty<String, Object> getProcessorProps ();

}
