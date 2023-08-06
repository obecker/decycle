plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.maven.publish.java)
}

description = "Java library for detecting and reporting package cycles"
val displayName by extra("Decycle Lib")

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

// see gradle/libs.versions.toml for libs.<xyz> dependencies
dependencies {
    implementation(libs.asm)
    implementation(libs.fontmetrics)
    implementation(libs.guava)
    implementation(libs.j2html)
    implementation(libs.maven.artifact)
    implementation(libs.slf4j.api)
    implementation(libs.webjars.locator) {
        exclude(group = "com.fasterxml.jackson.core") // not used
    }
    runtimeOnly(libs.bundles.webjars)

    testImplementation(libs.assertj)
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
    description = "Publishes decycle-lib to Maven Central via OSS Sonatype"
    dependsOn("publishMavenJavaPublicationToSonatypeRepository")
}
