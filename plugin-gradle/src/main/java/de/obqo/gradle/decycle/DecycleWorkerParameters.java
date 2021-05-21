package de.obqo.gradle.decycle;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

/**
 * @author Oliver Becker
 */
public interface DecycleWorkerParameters extends WorkParameters {

    Property<DecycleConfiguration> getConfiguration();

    Property<String> getClasspath();

    RegularFileProperty getReportFile();

}
