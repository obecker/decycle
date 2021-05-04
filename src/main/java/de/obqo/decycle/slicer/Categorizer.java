package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.Set;
import java.util.function.Function;

/**
 * A categorizer is a function that determines a set of categories for a given {@link Node}. Each category is again a
 * {@link Node}, however usually with a different node type ({@link Node#getType()}) than the original node.
 */
public interface Categorizer extends Function<Node, Set<Node>> {

    /**
     * Convenience constant to be used if a {@link de.obqo.decycle.slicer.Categorizer} doesn't return any categories for
     * a node.
     */
    Set<Node> NONE = Set.of();

    /**
     * Convenience constant for a {@link de.obqo.decycle.slicer.Categorizer} that never returns any categories
     */
    Categorizer EMPTY = __ -> NONE;

}
