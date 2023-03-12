package de.obqo.decycle.report;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.webjars.MultipleMatchesException;
import org.webjars.WebJarAssetLocator;

import j2html.tags.InlineStaticResource;
import j2html.utils.CSSMin;
import j2html.utils.JSMin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourcesExtractor {

    private static final WebJarAssetLocator locator = new WebJarAssetLocator();

    public static String createResourcesIfRequired(final File reportDir) throws IOException {
        final String resourcesDirName = "resources-" + ResourcesExtractor.class.getPackage().getImplementationVersion();
        final File resourcesDir = new File(reportDir, resourcesDirName);
        if (!resourcesDir.exists() && resourcesDir.mkdirs()) {
            copyResources(resourcesDir);
        }
        return resourcesDirName;
    }

    public static void copyResources(final File targetDir) throws IOException {
        copyLocalResource(targetDir, "custom.css");
        copyLocalResource(targetDir, "custom.js");

        copyWebJarResource(targetDir, "bootstrap", "bootstrap.min.css");
        copyWebJarResource(targetDir, "bootstrap-icons", "bootstrap-icons.css");
        copyWebJarResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff");
        copyWebJarResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff2");
        copyWebJarResource(targetDir, "jquery", "jquery.min.js");
        copyWebJarResource(targetDir, "tooltipster", "tooltipster.bundle.min.js");
        copyWebJarResource(targetDir, "tooltipster", "tooltipster.bundle.min.css");
        copyWebJarResource(targetDir, "tooltipster", "tooltipster-SVG.min.js");
        copyWebJarResource(targetDir, "svg.js", "svg.min.js");
    }

    private static void copyLocalResource(final File targetDir, final String name) throws IOException {
        String content = InlineStaticResource.getFileAsString("/report/" + name);
        if (name.endsWith(".css")) {
            content = CSSMin.compressCss(content);
        } else if (name.endsWith(".js")) {
            content = JSMin.compressJs(content);
        }
        copy(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), targetDir, name);
    }

    private static void copyWebJarResource(final File targetDir, final String webjar, final String asset)
            throws IOException {
        final String fullPath = getFullPath(webjar, asset);
        final InputStream inputStream = locator.getClass().getClassLoader().getResourceAsStream(fullPath);
        Objects.requireNonNull(inputStream, () -> String.format("Cannot read resource %s", fullPath));
        copy(inputStream, targetDir, asset);
    }

    private static String getFullPath(final String webjar, final String asset) {
        try {
            return locator.getFullPath(webjar, asset);
        } catch (final MultipleMatchesException ex) {
            // May happen if the classpath contains another webjar for the same asset in a different version
            // (possibly added by a maven or gradle plugin), see https://github.com/obecker/decycle/issues/16
            log.info("Decycle: Found multiple {} versions: {}", asset, ex.getMatches());
            // Take the latest webjar by comparing their versions.
            final String path = ex.getMatches().stream()
                    .flatMap(match -> Optional.ofNullable(WebJarAssetLocator.getWebJar(match))
                            .map(Map.Entry::getValue)
                            .map(version -> new SimpleEntry<>(new ComparableVersion(version), match))
                            .stream())
                    .max(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .orElseGet(() -> ex.getMatches().get(0)); // should not happen
            log.info("Decycle: use {}", path);
            return path;
        }
    }

    private static void copy(final InputStream inputStream, final File targetDir, final String name) throws IOException {
        final File targetFile = new File(targetDir, name);
        if (name.contains("/")) {
            targetFile.getParentFile().mkdirs();
        }

        try {
            Files.copy(inputStream, targetFile.toPath(), REPLACE_EXISTING);
        } catch (final FileAlreadyExistsException ignored) {
            // may happen if two threads (or gradle tasks) try to copy the same file concurrently
        }
    }
}
