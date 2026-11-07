package de.obqo.decycle.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider
/**
 * @author Oliver Becker
 */
class DecyclePlugin implements Plugin<Project> {

    private static final String TASK_NAME = "decycle"

    @Override
    void apply(final Project project) {
        project.apply plugin: 'java'

        Properties props = new Properties()
        getClass().classLoader.getResource("META-INF/gradle-plugins/de.obqo.decycle.properties").withInputStream { stream ->
            props.load(stream)
        }

        // create a runner task that runs all single decycle tasks
        final TaskProvider<Task> decycleRunnerTask = project.tasks.register(TASK_NAME) { Task task ->
            task.description = "Runs all decycle checks"
            task.group = JavaBasePlugin.VERIFICATION_GROUP
        }

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

            def sources = configuration.sourceSets
            if (sources.empty) {
                sources = project.sourceSets.asMap.values()
            }

            // create decycle work tasks, one for each source set
            sources.forEach { source ->
                def name = source.name

                TaskProvider<DecycleTask> decycleWorkTask =
                        project.tasks.register(TASK_NAME + name.capitalize(), DecycleTask) { DecycleTask task ->
                            task.description =
                                    "Checks the ${name} sources for package cycles and other custom constraints"
                            task.group = JavaBasePlugin.VERIFICATION_GROUP
                            task.configuration.set(configuration)
                            task.classpath.set(source.output)
                            task.reportFile.set(new File(project.buildDir, "reports/decycle/${name}.html"))
                            task.reportTitle.set(project.name + " | " + name)
                            task.workerClasspath.set(workerClasspath)
                            task.dependsOn(project.tasks.named(source.classesTaskName))
                        }

                // set task dependencies, e.g. decycle -> decycleTest -> testClasses
                decycleRunnerTask.configure { Task task ->
                    task.dependsOn(decycleWorkTask)
                }
            }

            // finally make task 'check' depend on the runner task
            project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { Task task ->
                task.dependsOn(decycleRunnerTask)
            }
        }
    }
}
