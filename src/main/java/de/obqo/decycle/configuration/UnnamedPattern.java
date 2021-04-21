package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.PatternMatchingCategorizer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnnamedPattern implements Pattern {

    private final String pattern;

    @Override
    public Categorizer toCategorizer(final String sliceType) {
        return new PatternMatchingCategorizer(sliceType, this.pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
