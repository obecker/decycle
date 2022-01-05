package de.obqo.decycle.gradle;

import java.io.File;
import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

/**
 * @author Oliver Becker
 */
@CacheableTask
public class DecycleTask extends DefaultTask {

    private final WorkerExecutor workerExecutor;

    private final Property<DecycleConfiguration> configuration;

    private final Property<FileCollection> classpath;

    private final Property<FileCollection> workerClasspath;

    private final RegularFileProperty reportFile;

    private final Property<String> reportTitle;

    @Inject
    public DecycleTask(final WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        final var objectFactory = getProject().getObjects();
        this.configuration = objectFactory.property(DecycleConfiguration.class);
        this.classpath = objectFactory.property(FileCollection.class);
        this.workerClasspath = objectFactory.property(FileCollection.class);
        this.reportFile = objectFactory.fileProperty();
        this.reportTitle = objectFactory.property(String.class);
    }

    @Input
    public Property<DecycleConfiguration> getConfiguration() {
        return this.configuration;
    }

    @SkipWhenEmpty
    @Classpath
    public Property<FileCollection> getClasspath() {
        return this.classpath;
    }

    @Classpath
    public Property<FileCollection> getWorkerClasspath() {
        return this.workerClasspath;
    }

    @OutputFile
    public RegularFileProperty getReportFile() {
        return this.reportFile;
    }

    @Internal
    public Property<String> getReportTitle() {
        return this.reportTitle;
    }

    @TaskAction
    public void runConstraintCheck() {
        if (this.classpath.get().filter(File::exists).isEmpty()) {
            final String message = this.classpath.get() + " are missing"; // "<sourceSet> classes are missing"
            getLogger().info("Decycle: {} - skipped {}", message, getName());
            throw new StopExecutionException(message);
        }

        final WorkQueue workQueue = this.workerExecutor.classLoaderIsolation(
                workerSpec -> workerSpec.getClasspath().from(this.workerClasspath.get()));
        workQueue.submit(DecycleWorker.class, parameters -> {
            parameters.getConfiguration().set(this.configuration);
            parameters.getClasspath().set(this.classpath.map(FileCollection::getAsPath));
            parameters.getReportFile().set(this.reportFile);
            parameters.getReportTitle().set(this.reportTitle);
        });
    }
}
