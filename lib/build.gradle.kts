plugins {
    `java-library`
    id("io.freefair.lombok")
    id("io.freefair.maven-publish-java")
}

description = "Java library for detecting and reporting package cycles"
val displayName by extra("Decycle Lib")

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

val asmVersion: String by project
val guavaVersion: String by project
val j2htmlVersion: String by project
val fontmetricsVersion: String by project
val webjarsVersion: String by project
val lombokVersion: String by project
val bootstrapVersion: String by project
val bootstrapIconsVersion: String by project
val jqueryVersion: String by project
val tooltipsterVersion: String by project
val svgjsVersion: String by project
val junitVersion: String by project
val assertjVersion: String by project
val slf4jVersion: String by project

dependencies {
    implementation("org.ow2.asm:asm:${asmVersion}")
    implementation("com.google.guava:guava:${guavaVersion}")
    implementation( "com.j2html:j2html:${j2htmlVersion}")
    implementation("org.javastack:fontmetrics:${fontmetricsVersion}")
    implementation("org.webjars:webjars-locator-core:${webjarsVersion}") {
        exclude(group = "com.fasterxml.jackson.core") // not used
    }
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    runtimeOnly("org.webjars:bootstrap:${bootstrapVersion}")
    runtimeOnly("org.webjars.npm:bootstrap-icons:${bootstrapIconsVersion}")
    runtimeOnly("org.webjars:jquery:${jqueryVersion}")
    runtimeOnly("org.webjars.npm:tooltipster:${tooltipsterVersion}")
    runtimeOnly("org.webjars:svg.js:${svgjsVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
    testRuntimeOnly("org.slf4j:slf4j-jdk14:${slf4jVersion}")
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
    version.set(lombokVersion)
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

tasks.register("copyGradleProperties", Copy::class) {
    from("${rootProject.projectDir}/gradle.properties")
    into("${sourceSets.main.get().output.resourcesDir}")
}

tasks.compileJava {
    dependsOn("copyGradleProperties")
}
