package de.obqo.decycle.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Becker
 */
public class DecyclePluginUnitTest {

    @Test
    void pluginShouldAddTaskAndExtension() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("de.obqo.decycle");

        assertThat(project.getTasks().getByName("decycle")).isInstanceOf(Task.class);
        assertThat(project.getExtensions().getByName("decycle")).isInstanceOf(DecycleExtension.class);
    }
}
