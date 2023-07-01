/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle;

import io.openapiprocessor.api.v2.OpenApiProcessorVersion;
import io.openapiprocessor.api.v2.Version;
import io.openapiprocessor.gradle.version.VersionCheck;
import org.gradle.api.logging.Logger;
import org.gradle.workers.WorkAction;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Runs the processor with the class loader that includes the dependencies from the openapiProcessor
 * configuration.
 */
@SuppressWarnings ("deprecation")
abstract public class OpenApiProcessorWorker implements WorkAction<OpenApiProcessorWorkParameters> {
    private final Logger log = (Logger) LoggerFactory.getLogger(OpenApiProcessorWorker.class);

    @Override
    public void execute () {
        Object processor = getProcessor (getProcessorName ());
        Map<String, ?> properties = getProcessorProperties ();

        try {
            check (processor);
            run (processor, properties);

        } catch (Throwable t) {
            waitForLogging ();
            throw t;
        }
    }

    private void check (Object processor) {
        try {
            if (!shouldCheck(processor))
                return;

            runCheck(processor);

        } catch (Throwable ignore) {
            // ignore, do not complain
        }
    }

    private void run (Object processor, Map<String, ?> properties) {
        if (processor instanceof io.openapiprocessor.api.v2.OpenApiProcessor) {
            run ((io.openapiprocessor.api.v2.OpenApiProcessor) processor, properties);

        } else if (processor instanceof io.openapiprocessor.api.v1.OpenApiProcessor) {
            run ((io.openapiprocessor.api.v1.OpenApiProcessor) processor, properties);

        } else if (processor instanceof io.openapiprocessor.api.OpenApiProcessor) {
            run ((io.openapiprocessor.api.OpenApiProcessor) processor, properties);

        } else if (processor instanceof com.github.hauner.openapi.api.OpenApiProcessor) {
            run ((com.github.hauner.openapi.api.OpenApiProcessor) processor, properties);
        }
    }

    private static void waitForLogging () {
        // without waiting gradle does not reliably log a processor error/exception.
        try {
            Thread.sleep (1000);
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
    }

    private boolean shouldCheck (Object processor) {
        if(! (processor instanceof io.openapiprocessor.api.v2.OpenApiProcessor)) {
            return false;
        }

        var theProcessor = (io.openapiprocessor.api.v2.OpenApiProcessor)processor;

        VersionCheck version = new VersionCheck(getRootDir(), getCheckUpdates());
        return version.canCheck(theProcessor.getName());
    }

    private void runCheck (Object processor) {
        if (! (processor instanceof OpenApiProcessorVersion)) {
            return;
        }

        var processorVersion = (OpenApiProcessorVersion) processor;

        if (processorVersion.hasNewerVersion ()) {
            String currentVersion = processorVersion.getVersion ();
            Version latestVersion = processorVersion.getLatestVersion ();

            log.quiet("{} version {} is available! I'm version {}.",
                getProcessorName (), latestVersion.getName (), currentVersion);
        }
    }

    private void run (io.openapiprocessor.api.v2.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private void run (io.openapiprocessor.api.v1.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private void run (io.openapiprocessor.api.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private void run (com.github.hauner.openapi.api.OpenApiProcessor processor, Map<String, ?> properties) {
        processor.run (properties);
    }

    private Object getProcessor (String processorName) {
        Object processor = ProcessorLoader.load (processorName, getClass ().getClassLoader ());
        if (processor == null) {
            throw new MissingProcessorException(processorName);
        }
        return processor;
    }

    private String getProcessorName() {
        return getParameters ().getProcessorName ().get ();
    }

    private Map<String, ?> getProcessorProperties() {
        return getParameters ().getProcessorProps ().get ();
    }

    private String getRootDir() {
        return getParameters ().getRootDir ().get ();
    }

    private String getCheckUpdates() {
        return getParameters ().getCheckUpdates ().get ();
    }
}
