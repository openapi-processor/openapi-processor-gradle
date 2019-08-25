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

package com.github.hauner.openapi.gradle

import com.github.hauner.openapi.gradle.generatr.TestGeneratr
import com.github.hauner.openapi.gradle.generatr.TestOptions
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification


class OpenApiGeneratrPluginSpec extends Specification {

    def logger = Mock (Logger)
    def container = Mock(ExtensionContainer)

    Closure taskAction
    Task task = Mock() {
        // fake action configuration
        doLast(_ as Closure) >> {
            taskAction = it.first() as Closure
            taskAction.delegate = task
            task
        }

        getLogger () >> logger
    }

    Closure taskConfig
    Project project = Mock {
        getExtensions () >> container

        // fake task configuration
        task (_, _ as Closure) >> {
            taskConfig = it.get(1) as Closure
            taskConfig.delegate = task
            taskConfig.call (task)
        }

        getLogger () >> logger
    }


    void "does not nothing when no generatr was found" () {
        GroovySpy (GeneratrLoader, global: true)
        GeneratrLoader.load () >> {
            []
        }

        when:
        def plugin = new OpenApiGeneratrPlugin()
        plugin.apply (project)

        then:
        0 * container.create (*_)
        0 * project.task (*_)
    }

    void "adds generatr extensions and tasks to gradle project when generatrs were found" () {
        GroovySpy (GeneratrLoader, global: true)
        GeneratrLoader.load () >> { [
            new TestGeneratr (name: 'one', options: TestOptions),
            new TestGeneratr (name: 'two', options: TestOptions)
        ] }

        when:
        def plugin = new OpenApiGeneratrPlugin()
        plugin.apply (project)

        then:
        1 * container.create ('generatrOne', TestOptions)
        1 * project.task ('generateOneApi', _ as Closure)
        1 * container.create ('generatrTwo', TestOptions)
        1 * project.task ('generateTwoApi', _ as Closure)
    }

    void "added generate task calls the generatr" () {
        def options = new TestOptions()
        container.create ('generatrTest', TestOptions) >> options

        TestGeneratr generatr = Mock {
            getName () >> 'test'
            getOptionsType () >> TestOptions
        }

        GroovySpy (GeneratrLoader, global: true)
        GeneratrLoader.load () >> { [generatr] }

        when:
        def plugin = new OpenApiGeneratrPlugin()
        plugin.apply (project)
        taskAction.call ()

        then:
        1 * generatr.run (options)
    }

    void "added generate task logs error when the generatr throws" () {
        task.getName () >> 'foo'

        def options = new TestOptions()
        container.create ('generatrTest', TestOptions) >> options

        TestGeneratr generatr = Mock {
            getName () >> 'test'
            getOptionsType () >> TestOptions
            run (options) >> {
                throw new Exception()
            }
        }

        GroovySpy (GeneratrLoader, global: true)
        GeneratrLoader.load () >> { [generatr] }

        when:
        def plugin = new OpenApiGeneratrPlugin()
        plugin.apply (project)
        taskAction.call ()

        then:
        1 * logger.error (task.name, _ as Exception)
    }
}
