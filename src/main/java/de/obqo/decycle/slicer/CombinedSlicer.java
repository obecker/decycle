package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.util.Assert;

import java.util.Objects;

/**
 * TODO same as {@link MultiCategorizer}, no?
 */
public class CombinedSlicer implements Categorizer {

    private final Categorizer[] categorizers;

    public CombinedSlicer(final Categorizer... categorizers) {
        Assert.notNull(categorizers, "Missing categorizers for CombinedSlicer");
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
