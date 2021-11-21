package de.obqo.decycle.report;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

import org.webjars.WebJarAssetLocator;

public class ResourcesExtractor {

    private static final WebJarAssetLocator locator = new WebJarAssetLocator();

    public static void copyWebJarResources(final File targetDir) throws IOException {
        copyResource(targetDir, "bootstrap", "bootstrap.min.css");
        copyResource(targetDir, "bootstrap-icons", "bootstrap-icons.css");
        copyResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff");
        copyResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff2");
        copyResource(targetDir, "jquery", "jquery.min.js");
        copyResource(targetDir, "tooltipster", "tooltipster.bundle.min.js");
        copyResource(targetDir, "tooltipster", "tooltipster.bundle.min.css");
        copyResource(targetDir, "tooltipster", "tooltipster-SVG.min.js");
        copyResource(targetDir, "svg.js", "svg.min.js");
    }

    private static void copyResource(final File targetDir, final String webjar, final String file) throws IOException {
        final File targetFile = new File(targetDir, file);
        targetFile.getParentFile().mkdirs();
        final String fullPath = locator.getFullPath(webjar, file);
        final InputStream inputStream = locator.getClass().getClassLoader().getResourceAsStream(fullPath);
        Objects.requireNonNull(inputStream, () -> String.format("Cannot read resource %s", fullPath));
        Files.copy(inputStream, targetFile.toPath(), REPLACE_EXISTING);
    }
}
