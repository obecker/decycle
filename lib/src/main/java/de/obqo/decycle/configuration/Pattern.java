package de.obqo.decycle.configuration;

import de.obqo.decycle.slicer.Categorizer;

public interface Pattern {

    Categorizer toCategorizer(String sliceType);
}
