group = "de.obqo.decycle"
version = "0.12.0-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "7.6"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}

tasks.register("clean") {
    delete("build")
}
