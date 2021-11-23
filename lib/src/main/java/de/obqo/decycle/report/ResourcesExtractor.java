package de.obqo.decycle.report;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

import org.webjars.WebJarAssetLocator;

import j2html.tags.InlineStaticResource;
import j2html.utils.CSSMin;
import j2html.utils.JSMin;

public class ResourcesExtractor {

    private static final WebJarAssetLocator locator = new WebJarAssetLocator();

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
        final File targetFile = new File(targetDir, name);
        targetFile.getParentFile().mkdirs();
        String content = InlineStaticResource.getFileAsString("/report/" + name);
        if (name.endsWith(".css")) {
            content = CSSMin.compressCss(content);
        } else if (name.endsWith(".js")) {
            content = JSMin.compressJs(content);
        }
        Files.copy(new ByteArrayInputStream(content.getBytes(UTF_8)), targetFile.toPath(), REPLACE_EXISTING);
    }

    private static void copyWebJarResource(final File targetDir, final String webjar, final String file)
            throws IOException {
        final File targetFile = new File(targetDir, file);
        targetFile.getParentFile().mkdirs();
        final String fullPath = locator.getFullPath(webjar, file);
        final InputStream inputStream = locator.getClass().getClassLoader().getResourceAsStream(fullPath);
        Objects.requireNonNull(inputStream, () -> String.format("Cannot read resource %s", fullPath));
        Files.copy(inputStream, targetFile.toPath(), REPLACE_EXISTING);
    }
}
