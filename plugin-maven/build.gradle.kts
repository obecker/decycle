plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.maven.plugin)
}

description = "Maven plugin that executes decycle dependency checks"
val displayName by extra("Decycle Maven Plugin")

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

// see gradle/libs.versions.toml for libs.<xyz> dependencies
dependencies {
    implementation(project(":decycle-lib"))
    implementation(libs.maven.core) {
        exclude(group = "com.google.guava")
    }
    implementation(libs.maven.plugin.tools)

    testImplementation(libs.assertj)
    testImplementation(libs.commons.io)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly(libs.slf4j.jdk14)
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Werror"))
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

lombok {
    version.set(libs.versions.lombok.get())
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

tasks.register("publishToSonatype") {
    group = "Publishing"
    description = "Publishes decycle-maven-plugin to Maven Central via OSS Sonatype"
    dependsOn("publishMavenJavaPublicationToSonatypeRepository")
}
