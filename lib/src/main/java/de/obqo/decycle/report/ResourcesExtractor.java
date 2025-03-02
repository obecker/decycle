package de.obqo.decycle.report;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

import j2html.tags.InlineStaticResource;
import j2html.utils.CSSMin;
import j2html.utils.JSMin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourcesExtractor {

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

        copyLibResource(targetDir, "bootstrap.min.css");
        copyLibResource(targetDir, "bootstrap.min.css.map");
        copyLibResource(targetDir, "bootstrap-icons.css");
        copyLibResource(targetDir, "fonts/bootstrap-icons.woff");
        copyLibResource(targetDir, "fonts/bootstrap-icons.woff2");
        copyLibResource(targetDir, "jquery.min.js");
        copyLibResource(targetDir, "tooltipster.bundle.min.css");
        copyLibResource(targetDir, "tooltipster.bundle.min.js");
        copyLibResource(targetDir, "tooltipster-SVG.min.js");
        copyLibResource(targetDir, "svg.min.js");
        copyLibResource(targetDir, "svg.min.js.map");
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

    private static void copyLibResource(final File targetDir, final String name) throws IOException {
        copy(ResourcesExtractor.class.getResourceAsStream("/libs/" + name), targetDir, name);
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
