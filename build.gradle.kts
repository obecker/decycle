group = "de.obqo.decycle"
version = "0.6.0-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "6.9"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}
