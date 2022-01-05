package de.obqo.decycle.maven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import de.obqo.decycle.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecycleMojoTest {

    private static final String REPORTING_DIR = "build/reporting";
    private static final String DECYCLE_TEST_DIR = REPORTING_DIR + "/decycle/test.html";
    private static final String DECYCLE_MAIN_DIR = REPORTING_DIR + "/decycle/main.html";

    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(new File(REPORTING_DIR));
    }

    @Test
    void shouldExecuteDecycleCheckMojo() throws MojoExecutionException, MojoFailureException {
        // given
        final String projectName = "DecycleCheckMojoTest";
        final DecycleCheckMojo decycleMojo = givenDecycleMojo(projectName, DecycleCheckMojo::new);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).exists()
                .content().startsWith("<html").contains(projectName + " | main - Decycle Report");
        assertThat(new File(DECYCLE_TEST_DIR)).exists()
                .content().startsWith("<html").contains(projectName + " | test - Decycle Report");
    }

    @Test
    void shouldExecuteDecycleCheckMainMojo() throws MojoExecutionException, MojoFailureException {
        // given
        final String projectName = "DecycleCheckMainMojoTest";
        final DecycleCheckMainMojo decycleMojo = givenDecycleMojo(projectName, DecycleCheckMainMojo::new);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).exists()
                .content().startsWith("<html").contains(projectName + " | main - Decycle Report");
        assertThat(new File(DECYCLE_TEST_DIR)).doesNotExist();
    }

    @Test
    void shouldExecuteDecycleCheckTestMojo() throws MojoExecutionException, MojoFailureException {
        // given
        final String projectName = "DecycleCheckTestMojoTest";
        final DecycleCheckTestMojo decycleMojo = givenDecycleMojo(projectName, DecycleCheckTestMojo::new);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).doesNotExist();
        assertThat(new File(DECYCLE_TEST_DIR)).exists()
                .content().startsWith("<html").contains(projectName + " | test - Decycle Report");
    }

    @Test
    void shouldSkipMainCheck() throws MojoExecutionException, MojoFailureException {
        // given
        final DecycleCheckMojo decycleMojo = givenDecycleCheckMojo();
        decycleMojo.setSkipMain(true);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).doesNotExist();
        assertThat(new File(DECYCLE_TEST_DIR)).exists();
    }

    @Test
    void shouldSkipTestCheck() throws MojoExecutionException, MojoFailureException {
        // given
        final DecycleCheckMojo decycleMojo = givenDecycleCheckMojo();
        decycleMojo.setSkipTest(true);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).exists();
        assertThat(new File(DECYCLE_TEST_DIR)).doesNotExist();
    }

    @Test
    void shouldSkipCheck() throws MojoExecutionException, MojoFailureException {
        // given
        final DecycleCheckMojo decycleMojo = givenDecycleCheckMojo();
        decycleMojo.setSkip(true);

        // when
        decycleMojo.execute();

        // then
        assertThat(new File(DECYCLE_MAIN_DIR)).doesNotExist();
        assertThat(new File(DECYCLE_TEST_DIR)).doesNotExist();
    }

    @Test
    void shouldFailForSlicingWithoutName() {
        // given
        final DecycleCheckMojo decycleCheckMojo = givenDecycleCheckMojo();
        final Slicing slicing = new Slicing();
        slicing.setPatterns("test.pattern");
        final Slicing[] slicings = { slicing };
        decycleCheckMojo.setSlicings(slicings);

        // when/then
        assertThatIllegalArgumentException().isThrownBy(decycleCheckMojo::execute)
                .withMessage("Missing name of slicing with patterns test.pattern");
    }

    @Test
    void shouldParseSlicingConfiguration() {
        // given
        final DecycleCheckMojo decycleCheckMojo = givenDecycleCheckMojo();
        final Slicing slicing1 = new Slicing();
        slicing1.setName("abc1");
        slicing1.setPatterns(" test.pattern.*=foo,     some.package.{*}.**");
        final Slicing slicing2 = new Slicing();
        slicing2.setName("abc2");
        slicing2.setPatterns(" some.package.bar.{**} ");
        final Slicing[] slicings = { slicing1, slicing2 };
        decycleCheckMojo.setSlicings(slicings);

        // when
        final Configuration configuration = buildConfiguration(decycleCheckMojo);

        // then
        assertThat(configuration).asString()
                .contains("slicings: {abc1=[test.pattern.*=foo, some.package.{*}.**], abc2=[some.package.bar.{**}]}");
    }

    @Test
    void shouldParseSlicingConstraints() {
        // given
        final DecycleCheckMojo decycleCheckMojo = givenDecycleCheckMojo();
        final Slicing slicing = new Slicing();
        slicing.setName("abc");
        slicing.setPatterns("some.package.{*}.**");
        final Allow allow = new Allow();
        allow.set("x,y,z");
        final AllowDirect allowDirect = new AllowDirect();
        allowDirect.setAnyOf("a,b");
        allowDirect.setAnyOf("c,d");
        allowDirect.setOneOf("x,y");
        allowDirect.setOneOf("z");
        final AllowConstraint[] constraints = { allow, allowDirect };
        slicing.setConstraints(constraints);
        final Slicing[] slicings = { slicing };
        decycleCheckMojo.setSlicings(slicings);

        // when
        final Configuration configuration = buildConfiguration(decycleCheckMojo);

        // then
        assertThat(configuration).asString()
                .contains("constraints: [x -> y -> z, (a, b) => (c, d) => [x, y] => z]");
    }

    private Configuration buildConfiguration(final DecycleCheckMojo decycleCheckMojo) {
        return decycleCheckMojo.buildConfiguration("build", "main", "resources", null);
    }

    private <M extends AbstractDecycleMojo> M givenDecycleMojo(final String projectName, final Supplier<M> factory) {
        final MavenProject project = givenMavenProject(projectName);
        final M decycleMojo = factory.get();
        decycleMojo.setProject(project);
        return decycleMojo;
    }

    private DecycleCheckMojo givenDecycleCheckMojo() {
        return givenDecycleMojo("dummy", DecycleCheckMojo::new);
    }

    private MavenProject givenMavenProject(final String name) {
        final Reporting reporting = new Reporting();
        reporting.setOutputDirectory(REPORTING_DIR);
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
