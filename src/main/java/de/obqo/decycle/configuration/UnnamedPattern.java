package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.PatternMatchingCategorizer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnnamedPattern implements Pattern {

    private final String pattern;

    @Override
    public Categorizer toCategorizer(final String slice) {
        return new PatternMatchingCategorizer(slice, this.pattern);
    }
}
