package de.obqo.decycle.gradle;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.Layer;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.check.SlicedConstraint;
import de.obqo.decycle.configuration.Configuration;
import de.obqo.decycle.configuration.NamedPattern;
import de.obqo.decycle.configuration.Pattern;
import de.obqo.decycle.configuration.UnnamedPattern;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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
        DecycleConfiguration configuration = getParameters().getConfiguration().get();
        String classpath = getParameters().getClasspath().get();
        File reportFile = getParameters().getReportFile().getAsFile().get();

        Configuration.ConfigurationBuilder builder = Configuration.builder();

        builder.classpath(classpath);
        builder.includes(configuration.getIncludings());
        builder.excludes(configuration.getExcludings());
        builder.ignoredDependencies(getIgnoredDependencies(configuration.getIgnoredDeps()));
        builder.slicings(
                configuration.getSlicings().stream().collect(
                        Collectors.toMap(SlicingConfiguration::getSliceType, this::getPatterns)));
        builder.constraints(
                configuration.getSlicings().stream().flatMap(
                        slConfig -> slConfig.getAllows().stream().map(allow -> getSlicedConstraint(slConfig, allow))
                ).collect(Collectors.toSet()));

        reportFile.getParentFile().mkdirs();

        try (final FileWriter writer = new FileWriter(reportFile)) {
            builder.report(writer);

            final Configuration decycleConfig = builder.build();

            logger.info("decycle configuration: {}", decycleConfig);

            final List<Constraint.Violation> violations = decycleConfig.check();

            logger.debug("decycle result: {}", violations);

            if (!violations.isEmpty()) {
                throw new GradleException(String.format("%s\n\nSee the report at: %s", violations, reportFile));
            }
        } catch (IOException ioException) {
            throw new GradleException(ioException.getMessage(), ioException);
        }
    }

    // Helper methods for converting the plugin's configuration (or extension) instances into decycle objects.
    // Note: the configuration/extension classes of the plugin must not depend on decycle directly, since the
    // decycle classes exist only on the classpath of this worker, but not on the runtime classpath of the plugin.

    private List<IgnoredDependency> getIgnoredDependencies(List<IgnoreConfig> ignoredDeps) {
        return ignoredDeps.stream()
                .map(ignoredDep -> new IgnoredDependency(ignoredDep.getFrom(), ignoredDep.getTo()))
                .collect(Collectors.toList());
    }

    private List<Pattern> getPatterns(SlicingConfiguration slicing) {
        return slicing.getPatterns().stream().map(pattern -> {
            // take care to map each NamedPatternConfig to a Decycle NamedPattern
            if (pattern instanceof NamedPatternConfig) {
                final NamedPatternConfig namedPattern = (NamedPatternConfig) pattern;
                return new NamedPattern(namedPattern.getName(), namedPattern.getPattern());
            } else {
                return new UnnamedPattern((String) pattern);
            }
        }).collect(Collectors.toList());
    }

    private SlicedConstraint getSlicedConstraint(final SlicingConfiguration slConfig, final AllowConfiguration allow) {
        return allow.isDirect() //
                ? new DirectLayeringConstraint(slConfig.getSliceType(), getSlices(allow))
                : new LayeringConstraint(slConfig.getSliceType(), getSlices(allow));
    }

    private List<Layer> getSlices(AllowConfiguration allow) {
        return Stream.of(allow.getSlices()).map(slice -> {
            // take care to map each LayerConfig to a Decycle Layer
            if (slice instanceof LayerConfig) {
                final LayerConfig layer = (LayerConfig) slice;
                return layer.isStrict() ? Layer.oneOf(layer.getSlices()) : Layer.anyOf(layer.getSlices());
            } else {
                return Layer.anyOf((String) slice);
            }
        }).collect(Collectors.toList());
    }
}
