package de.obqo.decycle.analysis;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.Categorizer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import com.google.common.base.Preconditions;

import org.objectweb.asm.ClassReader;

import lombok.SneakyThrows;

public class Analyzer {

    public Graph analyze(final String classpath, final Categorizer categorizer, final NodeFilter filter,
            final EdgeFilter ignoredEdgesFilter) {
        Preconditions.checkNotNull(classpath, "classpath must not be null");
        Preconditions.checkNotNull(categorizer, "categorizer must not be null");
        Preconditions.checkNotNull(filter, "filter must not be null");
        Preconditions.checkNotNull(ignoredEdgesFilter, "ignoredEdgesFilter must not be null");

        final Graph graph = new Graph(categorizer, filter, new NoSelfReference(categorizer), ignoredEdgesFilter);

        final String[] libs = classpath.split(System.getProperty("path.separator"));
        Stream.of(libs).flatMap(FileFinder::find).forEach(file -> analyze(file, graph));

        return graph;
    }

    @SneakyThrows
    private void analyze(final File file, final Graph graph) {
        if (file.getName().endsWith(".class")) {
            final var reader = new ClassReader(new BufferedInputStream(new FileInputStream(file)));
            readStream(reader, file.getName(), graph);
        } else {
            final var zipFile = new ZipFile(file);
            final var entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final var e = entries.nextElement();
                if (e.getName().endsWith(".class")) {
                    final var reader = new ClassReader(zipFile.getInputStream(e));
                    readStream(reader, e.getName(), graph);
                }
            }
        }
    }

    private void readStream(final ClassReader reader, final String name, final Graph graph) {
        try {
            reader.accept(new GraphBuildingClassVisitor(graph), 0);
        } catch (final Exception e) {
            System.err.println("Something went wrong when analyzing " + name);
            e.printStackTrace();
        }
    }
}
