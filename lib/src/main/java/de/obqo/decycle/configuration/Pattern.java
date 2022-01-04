package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;

public interface Pattern {

    Categorizer toCategorizer(String sliceType);

    static Pattern parse(final String string) {
        final String[] split = string.split("=", 2);
        return split.length == 2 ? new NamedPattern(split[0], split[1]) : new UnnamedPattern(string);
    }
}
