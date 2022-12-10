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

import lombok.SneakyThrows;

import org.webjars.WebJarAssetLocator;

import j2html.tags.InlineStaticResource;
import j2html.utils.CSSMin;
import j2html.utils.JSMin;

public class ResourcesExtractor {

    private static final Properties VERSION_PROPERTIES = loadVersionProperties();

    private static final String BOOTSTRAP_VERSION = VERSION_PROPERTIES.getProperty("bootstrapVersion");

    private static final String BOOTSTRAP_ICONS_VERSION = VERSION_PROPERTIES.getProperty("bootstrapIconsVersion");

    private static final String JQUERY_VERSION = VERSION_PROPERTIES.getProperty("jqueryVersion");

    private static final String TOOLTIPSTER_VERSION = VERSION_PROPERTIES.getProperty("tooltipsterVersion");

    private static final String SVGJS_VERSION = VERSION_PROPERTIES.getProperty("svgjsVersion");

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

        copyWebJarResource(targetDir, "bootstrap", String.format("%s/css/bootstrap.min.css", BOOTSTRAP_VERSION));
        copyWebJarResource(targetDir, "bootstrap-icons", String.format("%s/font/bootstrap-icons.css", BOOTSTRAP_ICONS_VERSION));
        copyWebJarResource(targetDir, "bootstrap-icons", String.format("%s/font/fonts/bootstrap-icons.woff", BOOTSTRAP_ICONS_VERSION));
        copyWebJarResource(targetDir, "bootstrap-icons", String.format("%s/font/fonts/bootstrap-icons.woff2", BOOTSTRAP_ICONS_VERSION));
        copyWebJarResource(targetDir, "jquery", String.format("%s/jquery.min.js", JQUERY_VERSION));
        copyWebJarResource(targetDir, "tooltipster", String.format("%s/dist/js/tooltipster.bundle.min.js", TOOLTIPSTER_VERSION));
        copyWebJarResource(targetDir, "tooltipster", String.format("%s/dist/css/tooltipster.bundle.min.css", TOOLTIPSTER_VERSION));
        copyWebJarResource(targetDir, "tooltipster", String.format("%s/dist/js/plugins/tooltipster/SVG/tooltipster-SVG.min.js", TOOLTIPSTER_VERSION));
        copyWebJarResource(targetDir, "svg.js", String.format("%s/svg.min.js", SVGJS_VERSION));
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

    private static void copyWebJarResource(final File targetDir, final String webjar, final String file)
            throws IOException {
        final String fullPath = locator.getFullPath(webjar, file);
        final InputStream inputStream = locator.getClass().getClassLoader().getResourceAsStream(fullPath);
        Objects.requireNonNull(inputStream, () -> String.format("Cannot read resource %s", fullPath));
        final File targetFile = getTargetFile(targetDir, file);
        Files.copy(inputStream, targetFile.toPath(), REPLACE_EXISTING);
    }

    private static File getTargetFile(final File targetDir, final String name) {
        final File targetFile = new File(targetDir, name);
        if (name.contains("/")) {
            targetFile.getParentFile().mkdirs();
        }
        return targetFile;
    }
}
