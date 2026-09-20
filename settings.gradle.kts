rootProject.name = "openapi-processor-gradle"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
//        mavenLocal()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
//            content {
//               includeGroup("io.openapiprocessor")
//            }
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        maven {
            name = "maven central snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            content {
               includeGroup("io.openapiprocessor")
            }
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

plugins {
  id("io.github.ben-manes.versions.settings") version "0.63.0"
}

include("processor-one")
include("processor-two")
include("processor-v1")
include("processor-v2")

//includeBuild '../openapi-processor-api'
