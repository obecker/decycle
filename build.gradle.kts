group = "de.obqo.decycle"
version = "0.4.0"

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
