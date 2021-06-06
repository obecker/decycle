plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.freefair.lombok") version "6.0.0-m2"
}

group = rootProject.group
version = rootProject.version

val junitVersion by extra("5.7.2")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.1")
    implementation("com.google.guava:guava:30.1.1-jre") {
        exclude(group = "org.checkerframework")
    }
    implementation( "com.j2html:j2html:1.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.19.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Werror"))
}

java {
    withSourcesJar()
    withJavadocJar()
}

lombok {
    version.set("1.18.20")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveVersion.set("${rootProject.version}")
    manifest {
        attributes["Implementation-Version"] = project.version
        attributes["Automatic-Module-Name"] = project.group
    }
}

publishing {
    repositories {
        maven {
            name = "local"
            url = file("$buildDir/repository").toURI()
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
        create<MavenPublication>("lib") {
            from(components["java"])
            pom {
                name.set("Decycle Lib")
                description.set("Java library for detecting and reporting package cycles")
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

signing {
    sign(publishing.publications["lib"])
}

tasks.withType<Sign>() {
    onlyIf {
        project.hasProperty("signing.keyId")
    }
}

