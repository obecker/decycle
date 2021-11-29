package de.obqo.decycle.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

class DecycleMojoTest {

    @Test
    void shouldExecuteDecycleMojo() throws MojoExecutionException, MojoFailureException {
        // given
        final String projectName = "DecycleMojoTest";
        final MavenProject project = givenMavenProject(projectName);
        final DecycleMojo decycleMojo = new DecycleMojo();
        decycleMojo.setProject(project);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File("build/reporting/decycle/main.html")).exists()
                .content().startsWith("<html").contains(projectName + " | main - Decycle Report");
        assertThat(new File("build/reporting/decycle/test.html")).exists()
                .content().startsWith("<html").contains(projectName + " | test - Decycle Report");
    }

    private MavenProject givenMavenProject(final String name) {
        final Reporting reporting = new Reporting();
        reporting.setOutputDirectory("build/reporting");
        final Build build = new Build();
        build.setOutputDirectory("build/classes/java/main");
        build.setTestOutputDirectory("build/classes/java/test");
        final Model model = new Model();
        model.setReporting(reporting);
        model.setBuild(build);
        final MavenProject mavenProject = new MavenProject();
        mavenProject.setModel(model);
        mavenProject.setName(name);
        return mavenProject;
    }
}
