rootProject.name = "buildSrc"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
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
        maven {
          url = uri("https://plugins.gradle.org/m2/")
        }
    }
}
