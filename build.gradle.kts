import io.openapiprocessor.build.core.dsl.initForProcessor
import io.openapiprocessor.build.core.dsl.projectGroupId
import io.openapiprocessor.build.core.dsl.signPublication
import io.openapiprocessor.build.core.dsl.sonatype

plugins {
    `kotlin-dsl`
    id("openapiprocessor.version")
    alias(libs.plugins.versions)
    id("compile")
    id("com.dorongold.task-tree") version "4.0.1"
    id("io.openapiprocessor.build.plugin.publish-central") version "2025.1-SNAPSHOT"
}

group = projectGroupId()
version = libs.versions.project.get()

// todo
extra["api"] = libs.versions.api.get()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val testInt by registering(JvmTestSuite::class) {
            sources {
                java {
                    setSrcDirs(listOf("src/testInt/groovy"))
                }
            }
        }

        withType<JvmTestSuite> {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(localGroovy())
                implementation(platform(libs.spock.platform))
                implementation(libs.spock.core)
                implementation(libs.bytebuddy)
                implementation(gradleTestKit())
            }
        }
    }
}

// compile groovy before kotlin
tasks.compileGroovy {
    classpath = sourceSets.main.get().compileClasspath
}

tasks.compileKotlin {
    libraries.from(sourceSets.main.get().groovy.classesDirectory)
}
//

tasks.named("publishToMavenCentral") {
    dependsOn("publishPluginMavenPublicationToStagingRepository")
}

tasks.named<Test>("testInt") {
    shouldRunAfter(tasks.named("test"))

    maxHeapSize = "2048M"
    systemProperty("PROJECT_DIR", "$projectDir")

    dependsOn(":processor-v1:build")
    dependsOn(":processor-one:build")
    dependsOn(":processor-two:build")
}

tasks.named("check") {
    dependsOn(testing.suites.named("testInt"))
}

dependencies {
    implementation(localGroovy())
    implementation(libs.snakeyaml)
}

gradlePlugin {
    website = "https://github.com/openapi-processor/openapi-processor-gradle"
    vcsUrl = "https://github.com/openapi-processor/openapi-processor-gradle"

    plugins {
        create("processorPlugin") {
            id = "io.openapiprocessor.openapi-processor"
            displayName = "Gradle openapi-processor plugin"
            description = "plugin to run openapi-processor-*, e.g. openapi-processor-spring (requires gradle 7.2+)"
            tags.set(listOf("openapi", "openapi-processor"))
            implementationClass = "io.openapiprocessor.gradle.OpenApiProcessorPlugin"
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            // update java-gradle-plugin publication
            named<MavenPublication>("pluginMaven") {
                pom.initForProcessor(project)
            }
        }

        repositories {
            sonatype(project)
        }
    }

    signing {
        signPublication(publishing.publications["pluginMaven"])
    }
}

/*
afterEvaluate {
    components.forEach { component ->
        println("component ${component.name}")
        println(component.toString())
    }

    publishing.publications.forEach { publication ->
        println("publication: ${publication.name}")
        println(publication.toString())
    }
}
 */
