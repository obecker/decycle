package de.obqo.decycle.analyze;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.Categorizer;
import lombok.SneakyThrows;

public class Analyzer {

    public Graph analyze(final String sourceFolder, final Categorizer categorizer, final Predicate<Node> filter) {
        final Graph graph = new Graph(categorizer, filter);

        final String[] libs = sourceFolder.split(System.getProperty("path.separator"));
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
