apply(plugin = "signing")

configure<PublishingExtension> {
    repositories {
        maven {
            name = "local"
            url = file("${rootProject.buildDir}/repository").toURI()
        }
        maven {
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications {
        // publication mavenJava is provided by the io.freefair.maven-publish-java plugin
        getByName<MavenPublication>("mavenJava").run {
            pom {
                name.set(project.extra["displayName"] as String)
                description.set(project.description)
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

tasks.withType<PublishToMavenRepository>() {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    if (repository.name == "sonatype" && isSnapshot) {
        enabled = false
    }
}

tasks.withType<Sign>() {
    onlyIf {
        project.hasProperty("signing.keyId")
    }
}

tasks.withType<Javadoc>() {
    doLast {
        // workaround for https://bugs.openjdk.java.net/browse/JDK-8215291 in Java 11
        file("${destinationDir}/search.js").appendText("getURLPrefix = function(ui) { return ''; };\n")
    }
    (options as StandardJavadocDocletOptions).links("https://docs.oracle.com/en/java/javase/11/docs/api/")
}
