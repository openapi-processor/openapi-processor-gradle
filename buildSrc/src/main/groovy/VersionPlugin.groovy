import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * provides a "generateVersion" task to a create a simple Version.java class:
 *
 * <pre>{@code
 * package io.openapiprocessor.gradle;
 *
 * public class Version {
 *     public static final String version = "${project.version}";
 *     public static final String api = "${project.ext.api}";
 * }
 * }</pre>
 *
 *
 * The io/openapiprocessor/gradle/Version.java file is generated to:
 *
 * $(project.buildDir}/version
 *
 * Add it as a source directory to include it in compilation.
 */
class VersionPlugin implements Plugin<Project> {

    void apply(Project project) {
        def task = project.tasks.register ('generateVersion', VersionTask, new Action<VersionTask>() {

            @Override
            void execute (VersionTask task) {
                task.buildFile = project.buildFile
                task.targetDir = "${project.buildDir}/version"
                task.version = project.version
                task.api = project.ext.api
            }

        })

        // adding the task as srcDir automatically executes the task before compiling.
        project.sourceSets {
          main {
            java {
                srcDirs task
            }
          }
        }
    }

}
