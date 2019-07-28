package com.github.hauner.openapi.gradle

import com.github.hauner.openapi.api.OpenApiGeneratr
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class OpenApiGeneratrPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {
        addGeneratrs (loadGeneratrs (project), project)
    }

    private Map<String, GeneratrData> addGeneratrs (LinkedHashMap<String, GeneratrData> generators, project) {
        generators.each { generatorEntry ->
            def name = generatorEntry.key
            def data = generatorEntry.value

            project.task ("generate${name.capitalize ()}Api") { Task task ->
                doLast {
                    try {
                        runGeneratr (data)
                    } catch (Exception e) {
                        logger.error (task.name, e)
                    }
                }
            }
        }
    }

    private runGeneratr (GeneratrData data) {
        data.generatr.run (data.options)
    }

    private LinkedHashMap<String, GeneratrData> loadGeneratrs (project) {
        Map<String, GeneratrData> generators = [:]

        ServiceLoader<OpenApiGeneratr> services = ServiceLoader.load (OpenApiGeneratr.class)
        services.each { generatr ->
            String name = generatr.name
            Class<?> options = generatr.optionsType
            def extension = project.extensions.create ("generatr${name.capitalize ()}", options)
            generators.put (name, new GeneratrData (generatr: generatr, options: extension))
        }

        generators
    }

    class GeneratrData {
        OpenApiGeneratr generatr
        def /* options type */ options
    }
}
