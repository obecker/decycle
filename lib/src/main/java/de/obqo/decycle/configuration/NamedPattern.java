package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NamedPatternMatchingCategorizer;

import lombok.extern.java.Log;

@Log
class NamedPattern implements Pattern {

    private final String pattern;
    private final String name;

    public NamedPattern(final String pattern, final String name) {

        if (name.contains("*") || name.contains(".")) {
            log.warning("Looks like you use the pattern '" + name +
                    "' as the NAME of a NamedPattern. Please note that a named pattern has the form <pattern>=<name>.");
        }

        this.name = name;
        this.pattern = pattern;
    }

    @Override
    public Categorizer toCategorizer(final String sliceType) {
        return new NamedPatternMatchingCategorizer(sliceType, this.pattern, this.name);
    }

    @Override
    public String toString() {
        return this.pattern + "=" + this.name;
    }
}
