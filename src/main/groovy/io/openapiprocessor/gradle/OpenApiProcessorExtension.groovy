/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * Extension object of the plugin. Used to configure the processors, e.g.
 * <pre>
 * openapiProcessor {
 *     ...
 *     apiPath "...."
 *
 *     spring {
 *       processor "..:...:..."
 *       targetDir "..."
 *
 *       .. other
 *     }
 *
 *     json {
 *       processor "..:...:..."
 *       targetDir "..."
 *
 *       .. other
 *     }
 *     ...
 *
 *     checkUpdates "never"|"daily"|"always"
 * }
 * </pre>
 */
abstract class OpenApiProcessorExtension extends OpenApiProcessorExtensionBase {

    /**
     * check automatically for updates. Can be "never"|"daily"|"always". Default is "never".
     */
    Property<String> checkUpdates

    /**
     * properties of the nested processor configurations by processor name, e.g.
     * <pre>
     *  openapiProcessor {
     *
     *      aProcessor {
     *          processor "..:...:..."
     *          targetDir "..."
     *
     *       .. other
     *          prop "abc"
     *          prop "xyz"
     *      }
     *
     *  }
     * </pre>
     */
    MapProperty<String, Processor> processors

    private Project project

    OpenApiProcessorExtension (Project project) {
        this.project = project
        checkUpdates = project.objects.property(String)
        processors = project.objects.mapProperty (String, Processor)

        checkUpdates.set("never")
    }

    /**
     * groovy/kotlin dsl. create a new processor configuration.
     *
     * <pre>
     *  openapiProcessor {
     *    process("newProcessor") { ... }
     * }
     * </pre>
     *
     * @param name unique name of the processor
     * @param args {@link Processor} action
     * @return the new processor
     */
    Processor process(String name, Action<Processor> action) {
        def processor = new Processor (name)
        action.execute (processor)
        processors.put (name, processor)
        processor
    }

    Processor process(GString name, Action<Processor> action) {
        process (name.toString (), action)
    }

    /**
     * groovy dsl only. create a new processor configuration.
     *
     * <pre>
     *  openapiProcessor {
     *    newProcessor { ... }
     * }
     * </pre>
     *
     * gradle will never call this from a kotlin build script unless explicitly calling it, i.e.
     *
     * <pre>
     *  openapiProcessor {
     *    methodMissing "newProcessor" { ... }
     * }
     * </pre>
     *
     * @param name unique name of the processor
     * @param args arg array. arg[0] must be a {@link Processor} configuration block
     * @return the new processor
     */
    def methodMissing (String name, def args) {
        def arg = args[0]

        // should be a nested processor configuration
        if (arg instanceof Closure) {
            // apply it to a new Processor () entry
            def processor = new Processor (name)

            processor.metaClass.file = { Object path ->
                return project.file(path)
            }

//            processor.metaClass.files = { Object... paths ->
//                return project.files(paths)
//            }

            processor.with(arg)
            processors.put (name, processor)
            return processor
        }

        throw new MissingMethodException(name, OpenApiProcessorExtension, args)
    }

    /**
     * set apiPath.
     */
    void apiPath(String apiPath) {
        this.apiPath.fileValue(new File(apiPath))
    }

    /**
     * set apiPath.
     */
    void apiPath(GString apiPath) {
        this.apiPath.fileValue(new File(apiPath))
    }

    void checkUpdates(String check) {
        checkUpdates.set(check)
    }

    Property<String> getCheckUpdates() {
        return checkUpdates
    }
}
