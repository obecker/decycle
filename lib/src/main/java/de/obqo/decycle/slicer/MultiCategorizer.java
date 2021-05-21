package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.Set;

import com.google.common.base.Preconditions;

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
        Preconditions.checkNotNull(categorizers, "Missing categorizers for combine");
        return new MultiCategorizer(categorizers);
    }

    @Override
    public Set<Node> apply(final Node node) {
        for (final Categorizer categorizer : this.categorizers) {
            final Set<Node> category = categorizer.apply(node);
            if (!category.isEmpty()) {
                return category;
            }
        }
        return NONE;
    }
}
