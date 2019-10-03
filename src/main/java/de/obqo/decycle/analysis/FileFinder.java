package de.obqo.decycle.analysis;

import de.obqo.decycle.util.Assert;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FileFinder {

    /**
     * @param rootPath absolute path of a directory
     * @return a {@link Stream} of all {@code *.class} and {@code *.jar} files in the directory specified by {@code
     * rootPath} and its sub directories
     */
    static Stream<File> find(final String rootPath) {
        return singleDirFind(new File(rootPath));
    }

    private static Stream<File> singleDirFind(final File root) {
        final Function<String, File> toFile = name -> new File(root, name);

        if (root.isDirectory()) {
            final String[] filenames = root.list();
            Assert.notNull(filenames, "returned filenames must not be null");

            final Stream<File> classFiles = Stream.of(filenames).filter(FileFinder::isClassFile).map(toFile);
            final Stream<File> dirs = Stream.of(filenames).map(toFile).filter(File::isDirectory);
            final Stream<File> nestedClassFiles = dirs.flatMap(FileFinder::singleDirFind);
            return Stream.concat(classFiles, nestedClassFiles);
        } else {
            return Stream.of(root).filter(file -> isClassFile(file.getName()));
        }
    }

    private static boolean isClassFile(final String name) {
        return name.endsWith(".class") || name.endsWith(".jar");
    }
}
