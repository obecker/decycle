package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NamedPatternMatchingCategorizer;

public class NamedPattern implements Pattern {

    private final String pattern;
    private final String name;

    public NamedPattern(final String pattern, final String name) {

        if (name.contains("*") || name.contains(".")) {
            System.out.println("You use '" + name + "' as the NAME of a NamedPattern. Please note that the NAME comes last in such a pattern.");
        }

        this.pattern = pattern;
        this.name = name;
    }

    @Override
    public Categorizer toCategorizer(final String slice) {
        return new NamedPatternMatchingCategorizer(slice, this.pattern, this.name);
    }
}
