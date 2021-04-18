plugins {
    java
    `maven-publish`
}

group = "de.obqo.decycle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion by extra("5.7.1")
val lombokVersion by extra("1.18.20")

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.ow2.asm:asm:7.3.1")
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

tasks.wrapper {
    gradleVersion = "6.8.3"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = project.version
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
