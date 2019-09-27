package de.obqo.decycle.analyze;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import de.obqo.decycle.graph.Graph;
import lombok.SneakyThrows;
import lombok.val;

public class Analyzer {

    public void analyze(String sourceFolder) {

        final String[] libs = sourceFolder.split(System.getProperty("path.separator"));
        Stream.of(libs).flatMap(FileFinder::find).forEach(this::analyze);

    }

    @SneakyThrows
    private void analyze(File file) {
        if (file.getName().endsWith(".class")) {
            val reader = new ClassReader(new BufferedInputStream(new FileInputStream(file)));
            readStream(reader, file.getName());
        } else {
            val zipFile = new ZipFile(file);
            val entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                val e = entries.nextElement();
                if (e.getName().endsWith(".class")) {
                    val reader = new ClassReader(zipFile.getInputStream(e));
                    readStream(reader, e.getName());
                }
            }
        }
    }

    private void readStream(ClassReader reader, String name) {
        try {
            final Graph graph = new Graph();
            reader.accept(new GraphBuildingClassVisitor(graph), 0);
            System.out.println("=========================");
            graph.topNodes().forEach(n -> System.out.println(n));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
