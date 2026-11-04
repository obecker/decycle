import java.util.Base64

plugins {
    signing
}

val ossrhHost = "https://ossrh-staging-api.central.sonatype.com"

configure<PublishingExtension> {
    repositories {
        maven {
            name = "local"
            url = rootProject.layout.buildDirectory.dir("repository").get().asFile.toURI()
        }
        maven {
            name = "sonatype"
            url = uri("$ossrhHost/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        // publication mavenJava is provided by the io.freefair.maven-publish-java plugin
        withType<MavenPublication>().matching { it.name == "mavenJava" }.configureEach {
            pom {
                name.set(provider { project.extra["displayName"] as String })
                description.set(provider { project.description })
                url.set("https://github.com/obecker/decycle")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("obecker")
                        name.set("Oliver Becker")
                    }
                }
                scm {
                    connection.set("https://github.com/obecker/decycle.git")
                    developerConnection.set("https://github.com/obecker/decycle.git")
                    url.set("https://github.com/obecker/decycle")
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    if (repository.name == "sonatype" && isSnapshot) {
        enabled = false
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf {
        project.hasProperty("signing.keyId")
    }
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).links("https://docs.oracle.com/en/java/javase/17/docs/api/")
}

val postToSonatype by tasks.registering {
    group = "publishing"
    description = "Finalizes the publication by sending a POST request to OSSRH"
    // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#1-modify-your-ci-script
    doLast {
        val ossrhUsername = System.getenv("OSSRH_USERNAME")
        val ossrhPassword = System.getenv("OSSRH_PASSWORD")
        val ossrhNamespace = "de.obqo"
        val url = "$ossrhHost/manual/upload/defaultRepository/$ossrhNamespace"
        val authToken = Base64.getEncoder().encodeToString("$ossrhUsername:$ossrhPassword".toByteArray())
        val authHeader = "Authorization: Bearer $authToken"
        val process = ProcessBuilder("curl", "-4", "--fail-with-body", "-X", "POST", "-H", authHeader, url)
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            project.logger.error(process.inputStream.bufferedReader().readText())
            throw GradleException("POST request to OSSRH failed (exit code: $exitCode)")
        }
    }
}

tasks.matching { it.name == "publishToSonatype" }.configureEach {
    finalizedBy(postToSonatype)
}
