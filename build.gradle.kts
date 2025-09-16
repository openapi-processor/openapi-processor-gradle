plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    alias(libs.plugins.publish)
    alias(libs.plugins.versions)
    id("compile")
}

group = "io.openapiprocessor"
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

//tasks.named("publishToMavenCentral") {
//    dependsOn("publishPluginMavenPublicationToStagingRepository")
//}

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


publishing {

}

publishingCentral {
    deploymentName = "gradle"
    waitFor = "VALIDATED"
}


//    publishing {
//        repositories {
//            sonatype(project)
//        }
//    }
//
//    val mavenPublications = publishing.publications.withType<MavenPublication>()
//    mavenPublications.all {
//        pom {
//            pom.initFrom(getPomProperties(project))
//        }
//    }
//
//    signing {
//        initSignKey()
//        sign(*mavenPublications.toTypedArray<Publication>())
//    }
