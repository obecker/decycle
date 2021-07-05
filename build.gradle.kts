group = "de.obqo.decycle"
version = "0.5.0-SNAPSHOT"

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
