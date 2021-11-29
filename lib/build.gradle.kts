plugins {
    `java-library`
    id("io.freefair.lombok")
}

description = "Java library for detecting and reporting package cycles"

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
    implementation("com.google.guava:guava:${guavaVersion}") {
        exclude(group = "org.checkerframework")
    }
    implementation( "com.j2html:j2html:${j2htmlVersion}")
    implementation("org.javastack:fontmetrics:${fontmetricsVersion}")
    implementation("org.webjars:webjars-locator-core:${webjarsVersion}") {
        exclude(group = "com.fasterxml.jackson.core") // not used
    }
    runtimeOnly("org.webjars:bootstrap:${bootstrapVersion}")
    runtimeOnly("org.webjars.npm:bootstrap-icons:${bootstrapIconsVersion}")
    runtimeOnly("org.webjars:jquery:${jqueryVersion}")
    runtimeOnly("org.webjars.npm:tooltipster:${tooltipsterVersion}")
    runtimeOnly("org.webjars:svg.js:${svgjsVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
    testImplementation("org.slf4j:slf4j-jdk14:${slf4jVersion}") // needed for fontmetrics
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

tasks.register("publishLib") {
    group = "Publishing"
    description = "Publishes decycle-lib to Maven Central via OSS Sonatype"
    dependsOn("publishJavaPublicationToSonatypeRepository")
}

