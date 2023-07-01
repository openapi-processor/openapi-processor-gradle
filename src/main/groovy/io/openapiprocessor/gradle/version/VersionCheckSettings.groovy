/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.gradle.version

import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.time.Instant

class VersionCheckSettings {
    private Yaml yaml
    private String settingsName

    private Map<String, String> settings

    VersionCheckSettings(String settingsName) {
        this.settingsName = settingsName
        this.settings = new HashMap<>()
        yaml = new Yaml()
    }

    Instant get(String name) {
        def at = settings.get(name)
        if (at == null) {
            return null
        }

        return Instant.parse(at)
    }

    void set(String name) {
        settings.put(name, Instant.now().toString())
    }

    void set(String name, Instant at) {
        settings.put(name, at.toString())
    }

    void read() {
        def file = new File(settingsName)
        if (file.exists()) {
            settings = yaml.loadAs(new FileReader(file), LinkedHashMap<String, String>)
        } else {
            settings = new LinkedHashMap<>()
        }
    }

    void write() {
        def file = new File(settingsName)
        Files.createDirectories(file.parentFile.toPath())
        yaml.dump(settings, new FileWriter(file))
    }
}
