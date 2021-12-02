package de.obqo.decycle.maven;

import de.obqo.decycle.check.Constraint;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal for performing decycle checks on the compiled classes of a project.
 * Intended to be used as a single goal 'mvn decycle:checkMain'.
 * More info: https://github.com/obecker/decycle
 */
@Mojo(name = "checkMain", threadSafe = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class DecycleCheckMainMojo extends AbstractDecycleMojo {

    @Override
    protected List<Constraint.Violation> executeCheck() throws IOException {
        return checkMain();
    }
}
