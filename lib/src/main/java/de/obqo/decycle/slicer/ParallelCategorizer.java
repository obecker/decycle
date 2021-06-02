package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Combines multiple categorizers by applying all of them and returns the union of all categories.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParallelCategorizer implements Categorizer {

    private final Categorizer[] categorizers;

    public static ParallelCategorizer parallel(final Categorizer... categorizers) {
        Preconditions.checkNotNull(categorizers, "Missing categorizers for ParallelCategorizer");
        return new ParallelCategorizer(categorizers);
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Stream.of(this.categorizers)
                .flatMap(categorizer -> categorizer.apply(node).stream())
                .collect(Collectors.toSet());
    }
}
