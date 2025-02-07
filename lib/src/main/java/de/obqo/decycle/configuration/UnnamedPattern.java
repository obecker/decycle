package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.PatternMatchingCategorizer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class UnnamedPattern implements Pattern {

    private final String pattern;

    public UnnamedPattern(final String pattern) {
        this.pattern = pattern;
        if (pattern != null && pattern.contains("(") && !pattern.contains("{")) {
            log.warn(
                    "Decycle: parentheses found instead of curly braces in slicing pattern '{}'. " +
                            "Please check if you need to migrate your patterns when upgrading from 0.7.0 to 0.8.0",
                    pattern);
        }
    }

    @Override
    public Categorizer toCategorizer(final String sliceType) {
        return new PatternMatchingCategorizer(sliceType, this.pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
