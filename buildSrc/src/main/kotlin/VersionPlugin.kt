import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

/**
 * provides a "generateVersion" task to create a simple Version.java class:
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
 * The io/openapiprocessor/gradle/Version.java file is generated to:
 *
 * $(project.buildDir}/version
 *
 * Add it added as a source directory to include it in compilation.
 */
class VersionPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val generateVersion = project.tasks.register<VersionTask>("generateVersion") {
            buildFile = project.buildFile
            targetDir = project.layout.buildDirectory.dir("version")
            pluginVersion = project.version.toString()
            apiVersion = project.extra["api"].toString()
        }

        val ssc = project.extensions.getByType<SourceSetContainer>()
        val main = ssc.named("main")
        main.configure {
            java.srcDir(generateVersion)
        }
    }
}
