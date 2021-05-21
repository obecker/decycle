package de.obqo.gradle.decycle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oliver Becker
 */
public class DecyclePluginUnitTest {

    @Test
    void pluginShouldAddTaskAndExtension() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("de.obqo.gradle.decycle");

        assertThat(project.getTasks().getByName("decycle")).isInstanceOf(Task.class);
        assertThat(project.getExtensions().getByName("decycle")).isInstanceOf(DecycleExtension.class);
    }
}
