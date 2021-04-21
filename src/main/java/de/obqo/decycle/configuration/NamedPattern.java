package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NamedPatternMatchingCategorizer;

public class NamedPattern implements Pattern {

    private final String name;
    private final String pattern;

    public NamedPattern(final String name, final String pattern) {

        if (name.contains("*") || name.contains(".")) {
            System.out.println("You use '" + name +
                    "' as the NAME of a NamedPattern. Please note that the NAME comes last in such a pattern.");
        }

        this.name = name;
        this.pattern = pattern;
    }

    @Override
    public Categorizer toCategorizer(final String sliceType) {
        return new NamedPatternMatchingCategorizer(sliceType, this.name, this.pattern);
    }

    @Override
    public String toString() {
        return this.name + "=" + this.pattern;
    }
}
