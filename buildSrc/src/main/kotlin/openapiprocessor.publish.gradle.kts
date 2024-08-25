plugins {
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        create<MavenPublication>("openapiprocessor") {
            pom {
                name = getGradleProperty("projectTitle")
                description = getGradleProperty("projectDesc")
                url = getGradleProperty("projectUrl")

                scm {
                   url = "https://github.com/${getGradleProperty("projectGithubRepo")}".toString ()
                }

                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }

                developers {
                    developer {
                        id = "hauner"
                        name  = "Martin Hauner"
                    }
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (isReleaseVersion()) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = getPublishUser()
                password = getPublishKey()
            }
        }
    }
}

// signing requires the sign key and pwd as environment variables:
//
// ORG_GRADLE_PROJECT_signKey=...
// ORG_GRADLE_PROJECT_signPwd=...

signing {
    setRequired({ gradle.taskGraph.hasTask("${project.path}:publishToSonatype") })

    val signKey: String? by project
    val signPwd: String? by project
    useInMemoryPgpKeys(signKey, signPwd)

    sign(publishing.publications["openapiprocessor"])
}
