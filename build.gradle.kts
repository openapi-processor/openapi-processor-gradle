import io.openapiprocessor.build.core.dsl.initForProcessor
import io.openapiprocessor.build.core.dsl.initSignKey
import io.openapiprocessor.build.core.dsl.projectGroupId
import io.openapiprocessor.build.core.dsl.sonatype

plugins {
    `kotlin-dsl`
    alias(libs.plugins.versions)
    alias(libs.plugins.publish)
    alias(libs.plugins.central)
    alias(libs.plugins.create)
    id("compile")
}

group = projectGroupId()
version = libs.versions.project.get()

versions {
    packageName = "io.openapiprocessor.gradle"
    entries = mapOf(
        "version" to libs.versions.project.get(),
        "api" to libs.versions.api.get()
    )
}

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
        repositories {
            sonatype(project)
        }
    }

    val mavenPublications = publishing.publications.withType<MavenPublication>()
    mavenPublications.all {
        pom {
            pom.initForProcessor(project)
        }
    }

    signing {
        initSignKey()
        sign(*mavenPublications.toTypedArray<Publication>())
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
