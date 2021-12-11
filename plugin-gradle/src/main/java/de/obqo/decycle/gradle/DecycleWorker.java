package de.obqo.decycle.gradle;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.Layer;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.check.SlicedConstraint;
import de.obqo.decycle.configuration.Configuration;
import de.obqo.decycle.configuration.Pattern;
import de.obqo.decycle.report.ResourcesExtractor;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.workers.WorkAction;

/**
 * @author Oliver Becker
 */
public abstract class DecycleWorker implements WorkAction<DecycleWorkerParameters> {

    private static final Logger logger = Logging.getLogger(Task.class);

    @Override
    public void execute() {
        final DecycleConfiguration configuration = getParameters().getConfiguration().get();
        final String classpath = getParameters().getClasspath().get();
        final File reportFile = getParameters().getReportFile().getAsFile().get();
        final String reportTitle = getParameters().getReportTitle().get();

        final Configuration.ConfigurationBuilder builder = Configuration.builder();

        builder.classpath(classpath);
        builder.including(configuration.getIncludings());
        builder.excluding(configuration.getExcludings());
        builder.ignoring(getIgnoredDependencies(configuration.getIgnoredDeps()));
        builder.slicings(
                configuration.getSlicings().stream().collect(
                        toMap(SlicingConfiguration::getSliceType, this::getPatterns)));
        builder.constraints(
                configuration.getSlicings().stream().flatMap(
                        slConfig -> slConfig.getAllows().stream().map(allow -> getSlicedConstraint(slConfig, allow))
                ).collect(toSet()));

        reportFile.getParentFile().mkdirs();

        try (final FileWriter writer = new FileWriter(reportFile)) {
            final String resourcesDirName = createResourcesIfRequired(reportFile);

            builder.report(writer);
            builder.reportResourcesPrefix(resourcesDirName);
            builder.reportTitle(reportTitle);

            final Configuration decycleConfig = builder.build();

            logger.info("decycle configuration: {}", decycleConfig);

            final List<Constraint.Violation> violations = decycleConfig.check();

            logger.debug("decycle result: {}", violations);

            if (!violations.isEmpty()) {
                final String message = String.format("%s\nSee the report at: %s",
                        Constraint.Violation.displayString(violations), reportFile);
                if (configuration.isIgnoreFailures()) {
                    logger.warn("Violations detected: {}", message);
                } else {
                    throw new GradleException(message);
                }
            }
        } catch (final IOException ioException) {
            throw new GradleException(ioException.getMessage(), ioException);
        }
    }

    private String createResourcesIfRequired(final File reportFile) throws IOException {
        final String resourcesDirName = "resources-" + Configuration.class.getPackage().getImplementationVersion();
        final File resourcesDir = new File(reportFile.getParentFile(), resourcesDirName);
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
            ResourcesExtractor.copyResources(resourcesDir);
        }
        return resourcesDirName;
    }

    // Helper methods for converting the plugin's configuration (or extension) instances into decycle objects.
    // Note: the configuration/extension classes of the plugin must not depend on decycle directly, since the
    // decycle classes exist only on the classpath of this worker, but not on the runtime classpath of the plugin.

    private List<IgnoredDependency> getIgnoredDependencies(final List<IgnoreConfig> ignoredDeps) {
        return ignoredDeps.stream()
                .map(ignoredDep -> new IgnoredDependency(ignoredDep.getFrom(), ignoredDep.getTo()))
                .collect(toList());
    }

    private List<Pattern> getPatterns(final SlicingConfiguration slicing) {
        return slicing.getPatterns().stream().map(Pattern::parse).collect(toList());
    }

    private SlicedConstraint getSlicedConstraint(final SlicingConfiguration slConfig, final AllowConfiguration allow) {
        return allow.isDirect() //
                ? new DirectLayeringConstraint(slConfig.getSliceType(), getSlices(allow))
                : new LayeringConstraint(slConfig.getSliceType(), getSlices(allow));
    }

    private List<Layer> getSlices(final AllowConfiguration allow) {
        return Stream.of(allow.getSlices()).map(this::mapLayer).collect(toList());
    }

    private Layer mapLayer(final Object slice) {
        // map each LayerConfig to a Decycle Layer
        if (slice instanceof LayerConfig) {
            final LayerConfig layer = (LayerConfig) slice;
            return layer.isStrict() ? Layer.oneOf(layer.getSlices()) : Layer.anyOf(layer.getSlices());
        } else {
            return Layer.anyOf((String) slice);
        }
    }
}
