package de.obqo.decycle.maven;

import de.obqo.decycle.check.Constraint;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal for performing decycle checks on the compiled classes (and test classes) of a project.
 * Intended to run automatically in the verify phase with
 * &lt;executions>&lt;execution>&lt;goals>&lt;goal>check&lt;/goal>&lt;/goals>&lt;/execution>&lt;/executions>
 * More info: https://github.com/obecker/decycle
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class DecycleCheckMojo extends AbstractDecycleMojo {

    @Override
    protected List<Constraint.Violation> executeCheck() throws IOException {
        final List<Constraint.Violation> mainViolations = checkMain();
        final List<Constraint.Violation> testViolations = checkTest();
        return Stream.of(mainViolations, testViolations).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
