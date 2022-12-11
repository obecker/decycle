package de.obqo.decycle.report;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

import com.google.common.base.Preconditions;

import org.webjars.MultipleMatchesException;
import org.webjars.WebJarAssetLocator;

import j2html.tags.InlineStaticResource;
import j2html.utils.CSSMin;
import j2html.utils.JSMin;
import lombok.SneakyThrows;

public class ResourcesExtractor {

    private static final Properties VERSION_PROPERTIES = loadVersionProperties();

    private static final String BOOTSTRAP_VERSION_PROP = "bootstrapVersion";
    private static final String BOOTSTRAP_ICONS_VERSION_PROP = "bootstrapIconsVersion";
    private static final String JQUERY_VERSION_PROP = "jqueryVersion";
    private static final String TOOLTIPSTER_VERSION_PROP = "tooltipsterVersion";
    private static final String SVGJS_VERSION_PROP = "svgjsVersion";

    @SneakyThrows(IOException.class)
    private static Properties loadVersionProperties() {
        final Properties versionProperties = new Properties();
        versionProperties.load(ResourcesExtractor.class.getResourceAsStream("/gradle.properties"));

        return versionProperties;
    }

    private static final WebJarAssetLocator locator = new WebJarAssetLocator();

    public static void copyResources(final File targetDir) throws IOException {
        targetDir.mkdirs();

        copyLocalResource(targetDir, "custom.css");
        copyLocalResource(targetDir, "custom.js");

        copyWebJarResource(targetDir, "bootstrap", "bootstrap.min.css", BOOTSTRAP_VERSION_PROP);
        copyWebJarResource(targetDir, "bootstrap-icons", "bootstrap-icons.css", BOOTSTRAP_ICONS_VERSION_PROP);
        copyWebJarResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff", BOOTSTRAP_ICONS_VERSION_PROP);
        copyWebJarResource(targetDir, "bootstrap-icons", "fonts/bootstrap-icons.woff2", BOOTSTRAP_ICONS_VERSION_PROP);
        copyWebJarResource(targetDir, "jquery", "jquery.min.js", JQUERY_VERSION_PROP);
        copyWebJarResource(targetDir, "tooltipster", "tooltipster.bundle.min.js", TOOLTIPSTER_VERSION_PROP);
        copyWebJarResource(targetDir, "tooltipster", "tooltipster.bundle.min.css", TOOLTIPSTER_VERSION_PROP);
        copyWebJarResource(targetDir, "tooltipster", "tooltipster-SVG.min.js", TOOLTIPSTER_VERSION_PROP);
        copyWebJarResource(targetDir, "svg.js", "svg.min.js", SVGJS_VERSION_PROP);
    }

    private static void copyLocalResource(final File targetDir, final String name) throws IOException {
        String content = InlineStaticResource.getFileAsString("/report/" + name);
        if (name.endsWith(".css")) {
            content = CSSMin.compressCss(content);
        } else if (name.endsWith(".js")) {
            content = JSMin.compressJs(content);
        }
        final File targetFile = getTargetFile(targetDir, name);
        Files.writeString(targetFile.toPath(), content, CREATE, TRUNCATE_EXISTING);
    }

    private static void copyWebJarResource(final File targetDir, final String webjar, final String asset,
            final String version)
            throws IOException {
        final String fullPath = getFullPath(webjar, asset, version);
        final InputStream inputStream = locator.getClass().getClassLoader().getResourceAsStream(fullPath);
        Objects.requireNonNull(inputStream, () -> String.format("Cannot read resource %s", fullPath));
        final File targetFile = getTargetFile(targetDir, asset);
        Files.copy(inputStream, targetFile.toPath(), REPLACE_EXISTING);
    }

    private static String getFullPath(final String webjar, final String asset, final String versionProp) {
        try {
            return locator.getFullPath(webjar, asset);
        } catch (final MultipleMatchesException ex) {
            // May happen if the classpath contains another webjar for the same asset in a different version
            // (possibly added by a maven or gradle plugin)
            final String version = VERSION_PROPERTIES.getProperty(versionProp);
            Preconditions.checkNotNull(version, "Missing property value for " + versionProp);
            final String versionPath = "/" + version + "/";
            return ex.getMatches().stream().filter(path -> path.contains(versionPath)).findFirst().orElseThrow(
                    () -> new IllegalStateException(
                            String.format("Unable to find %s for version %s", asset, version)));
        }
    }

    private static File getTargetFile(final File targetDir, final String name) {
        final File targetFile = new File(targetDir, name);
        if (name.contains("/")) {
            targetFile.getParentFile().mkdirs();
        }
        return targetFile;
    }
}
