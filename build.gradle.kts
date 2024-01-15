group = "de.obqo.decycle"
version = "1.2.0-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "8.2.1"
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
