package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NamedPatternMatchingCategorizer;

import lombok.extern.java.Log;

@Log
class NamedPattern implements Pattern {

    private final String name;
    private final String pattern;

    public NamedPattern(final String name, final String pattern) {

        if (name.contains("*") || name.contains(".")) {
            log.warning("Looks like you use the pattern '" + name +
                    "' as the NAME of a NamedPattern. Please note that a named pattern has the form <pattern>=<name>.");
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
        return this.pattern + "=" + this.name;
    }
}
