import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the

// see buildSrc/build.gradle.kts
val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

fun Project.isReleaseVersion(): Boolean {
    return ! version.toString().endsWith("SNAPSHOT")
}


fun Project.getBuildProperty(property: String): String {
    val propertyValue = findProperty(property)
    return if (propertyValue != null) {
        propertyValue as String
    } else {
        System.getenv(property) ?: "n/a"
    }
}

fun Project.getGradleProperty(property: String): Provider<String> {
    return providers.gradleProperty(property)
}

fun Project.getPublishUser(): String {
    return getBuildProperty("PUBLISH_USER")
}

fun Project.getPublishKey(): String {
    return getBuildProperty("PUBLISH_KEY")
}
