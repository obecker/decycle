plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.maven.publish.java)
}

description = "Java library for detecting and reporting package cycles"
val displayName by extra("Decycle Lib")

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

sourceSets.main {
    resources {
        srcDirs(layout.buildDirectory.dir("generated/resources"))
    }
}

val tooltipster: Configuration by configurations.creating

// see gradle/libs.versions.toml for libs.<xyz> dependencies
dependencies {
    implementation(libs.asm)
    implementation(libs.fontmetrics)
    implementation(libs.guava)
    implementation(libs.j2html)
    implementation(libs.maven.artifact)
    implementation(platform(libs.slf4j.bom))
    implementation("org.slf4j:slf4j-api")
    implementation(libs.webjars.locator) {
        exclude(group = "com.fasterxml.jackson.core") // not used
    }

    runtimeOnly(libs.bundles.runtime.webjars)

    tooltipster(libs.webjars.tooltipster)

    testImplementation(libs.assertj)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.slf4j:slf4j-jdk14")
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Werror"))
}

val extractTooltipster = tasks.register<Copy>("extractTooltipster") {
    // The tooltipster webjar contains outdated jQuery and jQuery UI versions that cause CVE warnings.
    // Work-around: extract the required tooltipster dist files and copy them into the local resources.
    from(zipTree(tooltipster.singleFile)) {
        includeEmptyDirs = false
        val included = setOf("tooltipster.bundle.min.css", "tooltipster.bundle.min.js", "tooltipster-SVG.min.js")
        eachFile {
            if (!included.contains(name)) {
                exclude()
            }
            // flatten the path (remove the source directory structure)
            relativePath = RelativePath(true, name)
        }
    }
    into(layout.buildDirectory.dir("generated/resources/libs"))
}

tasks.processResources {
    dependsOn(extractTooltipster)
}

tasks["sourcesJar"].dependsOn(extractTooltipster)

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
