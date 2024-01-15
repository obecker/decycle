package de.obqo.decycle.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin

/**
 * @author Oliver Becker
 */
class DecyclePlugin implements Plugin<Project> {

    private static final String TASK_NAME = "decycle"

    @Override
    void apply(final Project project) {
        Properties props = new Properties()
        getClass().classLoader.getResource("META-INF/gradle-plugins/de.obqo.decycle.properties")
                .withInputStream { stream ->
                    props.load(stream)
                }

        // create a runner task that runs all single decycle tasks
        final Task decycleRunnerTask = project.tasks.create(TASK_NAME)
        decycleRunnerTask.description = "Runs all decycle checks"
        decycleRunnerTask.group = JavaBasePlugin.VERIFICATION_GROUP

        final DecycleConfiguration configuration = new DecycleConfiguration()
        final String toolVersion = props.getProperty('tool-version')
        project.extensions.create(TASK_NAME, DecycleExtension, project, configuration)

        // create a new configuration that will be used by the plugin's worker
        final Configuration workerClasspath = project.configurations.create("decycle")
        workerClasspath.setDescription("The Decycle libraries to be used for this project")

        project.afterEvaluate {

            project.dependencies {
                decycle "de.obqo.decycle:decycle-lib:${toolVersion}"
            }

            def isAndroidProject = project.hasProperty("android")

            def sources = isAndroidProject ? configuration.androidSourceSets : configuration.sourceSets
            if (sources.empty) {
                def projectSources
                if (isAndroidProject) {
                    projectSources = project.android.sourceSets
                } else {
                    projectSources = project.findProperty("sourceSets")
                    if (projectSources == null) {
                        throw new GradleException("No source sets found. Did you forget to apply the 'java' plugin?")
                    }
                }
                sources = projectSources.asMap.values()
            }

            // create decycle work tasks, one for each source set
            sources.forEach { source ->
                def name = isAndroidProject ? adjustSourceSetName(source.name) : source.name
                def output = isAndroidProject
                        // TODO is there a better way to resolve the output (classes) directory in android projects?
                        ? project.files(project.layout.buildDirectory.dir("tmp/kotlin-classes/$name"),
                                        project.layout.buildDirectory.dir("intermediates/javac/$name"))
                        : source.output
                def compileTaskName = isAndroidProject
                        // TODO is there a better way to determine the compile task for each source set in android projects?
                        ? "compile" + name.capitalize() + "JavaWithJavac"
                        : source.classesTaskName
                def compileTask = project.tasks.findByPath(compileTaskName)
                if (compileTask != null) { // ignore android source sets without compile task
                    DecycleTask decycleWorkTask = project.tasks.create(TASK_NAME + name.capitalize(), DecycleTask)
                    decycleWorkTask.
                            description = "Checks the ${name} sources for package cycles and other custom constraints"
                    decycleWorkTask.group = JavaBasePlugin.VERIFICATION_GROUP
                    decycleWorkTask.configuration.set(configuration)
                    decycleWorkTask.classpath.set(output)
                    decycleWorkTask.reportFile.set(new File(project.buildDir, "reports/decycle/${name}.html"))
                    decycleWorkTask.reportTitle.set(project.name + " | " + name)
                    decycleWorkTask.workerClasspath.set(workerClasspath)

                    // set task dependencies, e.g. decycle -> decycleTest -> testClasses
                    decycleRunnerTask.dependsOn(decycleWorkTask.dependsOn(compileTask))
                } else {
                    project.getLogger().info(
                            "Decycle : Ignore sourceSet $name – cannot determine the corresponding compile task")
                }
            }

            // finally make task 'check' depend on the runner task
            project.tasks[JavaBasePlugin.CHECK_TASK_NAME].dependsOn decycleRunnerTask
        }
    }

    private static String adjustSourceSetName(String sourceSetName) {
        switch (sourceSetName) {
        case "testDebug": return "debugUnitTest"
        case "testRelease": return "releaseUnitTest"
        default: return sourceSetName
        }
    }
}
