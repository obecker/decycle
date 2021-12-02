package de.obqo.decycle.maven;

import de.obqo.decycle.check.Constraint;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal for performing decycle checks on the compiled test classes of a project.
 * Intended to be used as a single goal 'mvn decycle:checkTest'.
 * More info: https://github.com/obecker/decycle
 */
@Mojo(name = "checkTest", threadSafe = true)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class DecycleCheckTestMojo extends AbstractDecycleMojo {

    @Override
    protected List<Constraint.Violation> executeCheck() throws IOException {
        return checkTest();
    }
}
