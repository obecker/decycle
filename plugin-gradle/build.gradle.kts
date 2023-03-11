plugins {
    groovy
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
}

val pluginId = "de.obqo.decycle"

val junitVersion: String by project
val assertjVersion: String by project
val commonsioVersion: String by project

tasks.register("createClasspathManifest") {
    val outputDir = file("$buildDir/classpathManifest")
    val pluginClasspath = sourceSets.main.get().runtimeClasspath

    inputs.files(pluginClasspath)
    outputs.dir(outputDir)

    doLast {
        outputDir.mkdirs()
        file("$outputDir/plugin-classpath.txt").writeText(pluginClasspath.files.joinToString("\n"))
    }
}

dependencies {
    compileOnly(project(":decycle-lib"))

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("commons-io:commons-io:$commonsioVersion")
    testRuntimeOnly(files(tasks.getByName("createClasspathManifest")))
}

tasks.clean {
    delete("demo/build")
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.processResources {
    inputs.property("version", version)
    doLast {
        file("$buildDir/resources/main/META-INF/gradle-plugins/$pluginId.properties").appendText("tool-version=$version\n")
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

pluginBundle {
    website = "https://github.com/obecker/decycle/tree/master/plugin-gradle"
    vcsUrl = "https://github.com/obecker/decycle"
    tags = listOf("decycle", "code-quality")
}

gradlePlugin {
    plugins {
        create("decyclePlugin") {
            id = pluginId
            displayName = "Gradle Decycle Plugin"
            description = "Gradle plugin that executes decycle dependency checks"
            implementationClass = "de.obqo.decycle.gradle.DecyclePlugin"
        }
    }
}

tasks.publishPlugins {
    onlyIf {
        !"$version".endsWith("-SNAPSHOT")
    }
}
