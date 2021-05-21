plugins {
    java
    `maven-publish`
}

version = rootProject.version
group = rootProject.group

val artifactId by extra("${rootProject.name}-lib")
val junitVersion by extra("5.7.1")
val lombokVersion by extra("1.18.20")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

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

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set(artifactId)
    archiveVersion.set("${rootProject.version}")
    manifest {
        attributes["Implementation-Version"] = project.version
    }
}

tasks.build {
    dependsOn(":lib:publishMavenPublicationToLocalRepository")
}

publishing {
    repositories {
        maven {
            name = "local"
            url = file("../lib/build/repository").toURI()
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = "${project.extra["artifactId"]}"
            from(components["java"])
            pom {
                name.set("Decycle Lib")
            }
        }
    }
}
