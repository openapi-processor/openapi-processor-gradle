/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle

import com.github.hauner.openapi.api.OpenApiProcessor
import io.openapiprocessor.api.OpenApiProcessor as OpenApiProcessorV0
import io.openapiprocessor.api.v1.OpenApiProcessor as OpenApiProcessorV1
import io.openapiprocessor.api.v2.OpenApiProcessor as OpenApiProcessorV2
import io.openapiprocessor.api.v2.OpenApiProcessorVersion
import io.openapiprocessor.gradle.version.VersionCheck
import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.slf4j.LoggerFactory

/**
 * Runs the processor with the class loader that includes the dependencies from the openapiProcessor
 * configuration.
 */
@SuppressWarnings ("deprecation")
abstract class OpenApiProcessorWorker: WorkAction<OpenApiProcessorWorkParameters> {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name) as Logger

    @Override
    override fun execute() {
        val processor = getProcessor(getProcessorName())
        val properties = getProcessorProperties()

        try {
            check(processor)
            run(processor, properties);

        } catch (t: Throwable) {
            waitForLogging()
            throw t
        }
    }

    private fun check (processor: Any) {
        try {
            if (!shouldCheck(processor))
                return

            runCheck(processor)

        } catch (ignore: Throwable) {
            // ignore, do not complain
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun run(processor: Any, properties: Map<String, Any>) {
        when (processor) {
            is OpenApiProcessorV2 -> {
                processor.run(properties as MutableMap<String, *>)
            }
            is OpenApiProcessorV1 -> {
                processor.run(properties as MutableMap<String, *>)
            }
            is OpenApiProcessorV0 -> {
                processor.run(properties as MutableMap<String, *>)
            }
            is OpenApiProcessor -> {
                processor.run(properties as MutableMap<String, *>)
            }
        }
    }

    private fun shouldCheck (processor: Any): Boolean {
        if(processor !is OpenApiProcessorV2) {
            return false
        }

        val version = VersionCheck(getRootDir(), getCheckUpdates())
        return version.canCheck(processor.name)
    }

    private fun runCheck(processor: Any) {
        if (processor !is OpenApiProcessorVersion) {
            return
        }

        if (processor.hasNewerVersion()) {
            val currentVersion = processor.version
            val latestVersion = processor.latestVersion

            log.quiet("{} version {} is available! I'm version {}.",
                getProcessorName(), latestVersion.name, currentVersion)
        }
    }

    private fun getProcessor (processorName: String): Any {
        val processor = ProcessorLoader.load (processorName, javaClass.getClassLoader ())
        if (processor == null) {
            throw MissingProcessorException(processorName)
        }
        return processor
    }

    private fun getProcessorName(): String {
        return parameters.getProcessorName().get()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getProcessorProperties(): Map<String, Any> {
        return this.parameters.getProcessorProps().get()
    }

    private fun getRootDir(): String {
        return parameters.getRootDir().get()
    }

    private fun getCheckUpdates(): String {
        return parameters.getCheckUpdates().get()
    }

    companion object {
        private fun waitForLogging () {
            // without waiting gradle does not reliably log a processor error/exception.
            try {
                Thread.sleep (1000)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }
}
