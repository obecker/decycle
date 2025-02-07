package de.obqo.decycle.configuration;

import static java.lang.String.format;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NamedPatternMatchingCategorizer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class NamedPattern implements Pattern {

    private final String pattern;
    private final String name;

    public NamedPattern(final String pattern, final String name) {

        if (name.contains("*") || name.contains(".")) {
            log.warn("Decycle: Looks like you use the pattern '{}' as the NAME of a NamedPattern. " +
                    "Please note that a named pattern has the form <pattern>=<name>.", name);
        }
        if (pattern.contains("{") || pattern.contains("}")) {
            throw new IllegalArgumentException(
                    format("Curly braces are not allowed in a named pattern. Encountered '%s'", pattern));
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
