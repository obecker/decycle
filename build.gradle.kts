group = "de.obqo.decycle"
version = "1.2.8-SNAPSHOT"

tasks.wrapper {
    gradleVersion = "8.2.1"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.register<Delete>("clean") {
    description = "Deletes the build directory"
    group = "Build"
    delete(layout.buildDirectory)
}
