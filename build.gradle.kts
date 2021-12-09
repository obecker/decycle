group = "de.obqo.decycle"
version = "0.7.0-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "7.3"
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
