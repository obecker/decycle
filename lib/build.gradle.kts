import org.gradle.kotlin.dsl.dependencies
import org.webjars.WebJarAssetLocator
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.maven.publish.java)
}

description = "Java library for detecting and reporting package cycles"
val displayName by extra("Decycle Lib")

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(libs.webjars.locator) {
            exclude(group = "com.fasterxml.jackson.core") // not used
        }
        classpath(libs.bundles.webjars)
    }
}

val generatedResources = layout.buildDirectory.dir("generated/resources")
sourceSets.main {
    resources {
        srcDirs(generatedResources)
    }
}

// see gradle/libs.versions.toml for libs.<xyz> dependencies
dependencies {
    implementation(libs.asm)
    implementation(libs.fontmetrics)
    implementation(libs.guava)
    implementation(libs.j2html)
    implementation(libs.maven.artifact)
    implementation(platform(libs.slf4j.bom))
    implementation("org.slf4j:slf4j-api")

    testImplementation(libs.assertj)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

val extractWebJars = tasks.register("extractWebjars") {
    description = "Extract webjar resources to generated/resources"

    val locator = WebJarAssetLocator()
    val classLoader = locator::class.java.classLoader
    val resourcesLibs = generatedResources.map { it.dir("libs") }

    fun copyWebJarResource(webJar: String, resource: String) {
        logger.info("Copying resource $resource from $webJar")
        val stream = requireNotNull(classLoader.getResourceAsStream(locator.getFullPath(webJar, resource))) {
            "Missing $resource in $webJar"
        }
        val target = resourcesLibs.get().file(resource).asFile.also {
            it.parentFile.mkdirs()
        }.toPath()
        Files.copy(stream, target, REPLACE_EXISTING)
    }

    doLast {
        copyWebJarResource("bootstrap", "bootstrap.min.css")
        copyWebJarResource("bootstrap", "bootstrap.min.css.map")
        copyWebJarResource("bootstrap-icons", "bootstrap-icons.css")
        copyWebJarResource("bootstrap-icons", "fonts/bootstrap-icons.woff")
        copyWebJarResource("bootstrap-icons", "fonts/bootstrap-icons.woff2")
        copyWebJarResource("jquery", "jquery.min.js")
        copyWebJarResource("tooltipster", "tooltipster.bundle.min.css")
        copyWebJarResource("tooltipster", "tooltipster.bundle.min.js")
        copyWebJarResource("tooltipster", "tooltipster-SVG.min.js")
        copyWebJarResource("svg.js", "svg.min.js")
        copyWebJarResource("svg.js", "svg.min.js.map")
    }
}

tasks.processResources {
    dependsOn(extractWebJars)
}

tasks["sourcesJar"].dependsOn(extractWebJars)

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
