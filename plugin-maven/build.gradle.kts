plugins {
    `java-library`
    id("io.freefair.lombok")
    id("io.freefair.maven-plugin")
}

val junitVersion: String by project
val assertjVersion: String by project
val lombokVersion: String by project
val mavenVersion: String by project
val mavenPluginToolsVersion: String by project

dependencies {
    implementation(project(":decycle-lib"))
    implementation("org.apache.maven:maven-core:${mavenVersion}") {
        exclude(group = "com.google.guava")
    }
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:${mavenPluginToolsVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
}

lombok {
    version.set(lombokVersion)
}

tasks.test {
    useJUnitPlatform()
}
