package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Combines multiple categorizers by applying all of them and returns the union of all categories.
 */
public class ParallelCategorizer implements Categorizer {

    private final Categorizer[] cs;

    public ParallelCategorizer(final Categorizer... cs) {
        Assert.notNull(cs, "Missing categorizers for ParallelCategorizer");
        this.cs = cs;
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Stream.of(this.cs)
                .flatMap(categorizer -> categorizer.apply(node).stream())
                .collect(Collectors.toSet());
    }
}
