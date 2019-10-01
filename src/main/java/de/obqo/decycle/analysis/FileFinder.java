package de.obqo.decycle.analysis;

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
    static Stream<File> find(String rootPath) {
        return singleDirFind(new File(rootPath));
    }

    private static Stream<File> singleDirFind(final File root) {
        final Function<String, File> toFile = name -> new File(root, name);

        if (root.isDirectory()) {
            final String[] filenames = root.list();
            assert filenames != null;

            Stream<File> classFiles = Stream.of(filenames).filter(FileFinder::isClassFile).map(toFile);
            Stream<File> dirs = Stream.of(filenames).map(toFile).filter(File::isDirectory);
            Stream<File> nestedClassFiles = dirs.flatMap(FileFinder::singleDirFind);
            return Stream.concat(classFiles, nestedClassFiles);
        } else {
            return Stream.of(root).filter(file -> isClassFile(file.getName()));
        }
    }

    private static boolean isClassFile(String name) {
        return name.endsWith(".class") || name.endsWith(".jar");
    }
}
