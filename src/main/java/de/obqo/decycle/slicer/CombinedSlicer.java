package de.obqo.decycle.slicer;

import java.util.Objects;

import de.obqo.decycle.model.Node;

/**
 * TODO same as {@link MultiCategorizer}, no?
 */
public class CombinedSlicer implements Categorizer {

    private final Categorizer[] categorizers;

    public CombinedSlicer(final Categorizer... categorizers) {
        assert categorizers != null;
        this.categorizers = categorizers;
    }

    @Override
    public Node apply(final Node node) {
        Node acc = node;
        for (final Categorizer categorizer : this.categorizers) {
            acc = !Objects.equals(acc, node) ? acc : categorizer.apply(node);
        }
        return acc;
    }
}
