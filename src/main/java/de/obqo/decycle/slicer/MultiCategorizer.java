package de.obqo.decycle.slicer;

import java.util.Objects;

import de.obqo.decycle.model.Node;

/**
 * Combines multiple Categorizers to a single one by applying one after the other until one succeeds to categorize the node.
 */
public class MultiCategorizer implements Categorizer {

    private final Categorizer[] categorizers;

    public MultiCategorizer(final Categorizer... categorizers) {
        assert categorizers != null;
        this.categorizers = categorizers;
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
