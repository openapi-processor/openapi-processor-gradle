rootProject.name = "buildSrc"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
//        maven {
//            url = uri("https://central.sonatype.com/repository/maven-snapshots")
//        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        mavenCentral()
//        maven {
//            url = uri("https://central.sonatype.com/repository/maven-snapshots")
//        }
//        maven {
//          url = uri("https://plugins.gradle.org/m2/")
//        }
    }
}
