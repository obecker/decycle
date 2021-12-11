package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;

public interface Pattern {

    Categorizer toCategorizer(String sliceType);

    static Pattern parse(final String string) {
        final String[] split = string.split("=", 2);
        return split.length == 2 ? new NamedPattern(split[1], split[0]) : new UnnamedPattern(string);
    }
}
