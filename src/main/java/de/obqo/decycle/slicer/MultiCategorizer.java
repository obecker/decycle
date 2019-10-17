package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.util.Assert;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Combines multiple Categorizers to a single one by applying one after the other until one succeeds to categorize the
 * node.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiCategorizer implements Categorizer {

    private final Categorizer[] categorizers;

    public static Categorizer combine(final Categorizer... categorizers) {
        Assert.notNull(categorizers, "Missing categorizers for combine");
        return new MultiCategorizer(categorizers);
    }

    @Override
    public Node apply(final Node node) {
        for (final Categorizer categorizer : this.categorizers) {
            final Node category = categorizer.apply(node);
            if (!Objects.equals(category, node)) {
                return category;
            }
        }
        return node;
    }
}
