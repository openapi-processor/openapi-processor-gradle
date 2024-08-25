plugins {
    `kotlin-dsl`
    id("signing")
    id("openapiprocessor.version")
    id("openapiprocessor.publish")
    alias(libs.plugins.publish)
    alias(libs.plugins.nexus)
    alias(libs.plugins.versions)
}

group = getGradleProperty("projectGroupId")
version = getGradleProperty("projectVersion")
extra["api"] = libs.versions.api.get()

allprojects {
    apply(plugin = "groovy")

    repositories {
        mavenCentral()

        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            content {
               includeGroup("io.openapiprocessor")
            }
            mavenContent {
                snapshotsOnly()
            }
        }
    }

    dependencies {
        compileOnly(rootProject.libs.openapi.processor.api)
        implementation(localGroovy())
    }
}

java {
    withJavadocJar()
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val testInt by registering(JvmTestSuite::class) {
            testType = TestSuiteType.INTEGRATION_TEST

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
            description = "plugin to run openapi-processor-*, e.g. openapi-processor-spring (requires gradle 7.0+, with gradle 5.5+ use 2021.3)"
            tags.set(listOf("openapi", "openapi-processor"))
            implementationClass = "io.openapiprocessor.gradle.OpenApiProcessorPlugin"
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username = getPublishUser()
            password = getPublishKey()
        }
    }
}
