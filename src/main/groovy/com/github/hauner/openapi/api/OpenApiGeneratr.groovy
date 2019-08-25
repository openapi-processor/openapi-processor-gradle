/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.api

/**
 * To make an openapi-generatr available to the generatr-gradle plugin it must
 * - implement this interface
 * - provide a META-INF/services/com.github.hauner.openapi.api.OpenApiGeneratr property file in
 *   its resources with the class name of the implementing class.
 *
 * @author Martin Hauner
 */
interface OpenApiGeneratr<T> {

    /**
     * The identifying name of the generatr. *Should* be globally unique so a consumer of this
     * interface can distinguish between different generatrs.
     *
     * @return the unique name of the generatr
     */
    String getName()

    /**
     * the type of the generatr options given to the run() method.
     *
     * @return options class
     */
    Class<T> getOptionsType()

    /**
     * Runs the generatr with the given options.
     *
     * @param options the generatr configuration
     */
    void run(T options)

}
