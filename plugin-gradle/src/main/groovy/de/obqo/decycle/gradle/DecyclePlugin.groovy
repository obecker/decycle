package de.obqo.decycle.gradle

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
        getClass().classLoader.getResource("META-INF/gradle-plugins/de.obqo.decycle.properties").withInputStream { stream ->
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
            // Ensure that a compatible plugin (java, java-library, android, etc.) is applied
            if (!project.hasProperty('sourceSets')) {
                throw new org.gradle.api.GradleException(
                    "Decycle plugin requires a plugin that provides source sets (e.g., 'java', 'java-library', or Android plugins). " +
                    "Please apply one of these plugins before applying the decycle plugin."
                )
            }

            project.dependencies {
                decycle "de.obqo.decycle:decycle-lib:${toolVersion}"
            }

            def sources = configuration.sourceSets
            if (sources.empty) {
                sources = project.sourceSets.asMap.values()
            }

            // create decycle work tasks, one for each source set
            sources.forEach { source ->
                def name = source.name

                DecycleTask decycleWorkTask = project.tasks.create(TASK_NAME + name.capitalize(), DecycleTask)
                decycleWorkTask.
                        description = "Checks the ${name} sources for package cycles and other custom constraints"
                decycleWorkTask.group = JavaBasePlugin.VERIFICATION_GROUP
                decycleWorkTask.configuration.set(configuration)
                decycleWorkTask.classpath.set(source.output)
                decycleWorkTask.reportFile.set(new File(project.buildDir, "reports/decycle/${name}.html"))
                decycleWorkTask.reportTitle.set(project.name + " | " + name);
                decycleWorkTask.workerClasspath.set(workerClasspath)

                // set task dependencies, e.g. decycle -> decycleTest -> testClasses
                decycleRunnerTask.dependsOn(decycleWorkTask.dependsOn(project.tasks[source.classesTaskName]))
            }

            // finally make task 'check' depend on the runner task
            project.tasks[JavaBasePlugin.CHECK_TASK_NAME].dependsOn decycleRunnerTask
        }
    }
}
