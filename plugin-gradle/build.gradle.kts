plugins {
    groovy
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.publish)
}

val pluginId = "de.obqo.decycle"

tasks.register("createClasspathManifest") {
    val outputDir = layout.buildDirectory.dir("classpathManifest")
    val outputFile = outputDir.map { it.file("plugin-classpath.txt") }
    val pluginClasspath = sourceSets.main.get().runtimeClasspath

    inputs.files(pluginClasspath)
    outputs.dir(outputDir)

    doLast {
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(pluginClasspath.files.joinToString("\n"))
        }
    }
}

// see gradle/libs.versions.toml for libs.<xyz> dependencies
dependencies {
    compileOnly(project(":decycle-lib"))

    testImplementation(gradleTestKit())
    testImplementation(libs.assertj)
    testImplementation(libs.commons.io)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly(files(tasks.named("createClasspathManifest")))
}

tasks.clean {
    delete("demo/build")
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:-missing", true)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.processResources {
    inputs.property("version", version)
    doLast {
        layout.buildDirectory.file("resources/main/META-INF/gradle-plugins/$pluginId.properties")
            .get().asFile.appendText("tool-version=$version\n")
    }
}

tasks.test {
    useJUnitPlatform()
    inputs.files(project(":decycle-lib").fileTree("build/classes") { // add lib because there's no runtime dependency
        include("*/main/**")
    })
    inputs.files(fileTree("demo") {
        include("*.gradle")
        include("*.gradle.kts")
        include("src/**")
    })
    // we need a deployed library for testing the plugin
    dependsOn(":decycle-lib:publishMavenJavaPublicationToLocalRepository")
}

gradlePlugin {
    website.set("https://github.com/obecker/decycle/tree/master/plugin-gradle")
    vcsUrl.set("https://github.com/obecker/decycle")
    plugins {
        create("decyclePlugin") {
            id = pluginId
            displayName = "Gradle Decycle Plugin"
            description = "Gradle plugin that executes decycle dependency checks"
            implementationClass = "de.obqo.decycle.gradle.DecyclePlugin"
            tags.set(listOf("decycle", "code-quality"))
        }
    }
}

tasks.publishPlugins {
    onlyIf {
        !"$version".endsWith("-SNAPSHOT")
    }
}
