package de.obqo.decycle.model;

import java.util.function.BiPredicate;

public interface EdgeFilter extends BiPredicate<Node, Node> {

    /**
     * A filter that matches all edges.
     */
    EdgeFilter ALL = (n, m) -> true;

    /**
     * A filter that matches no edges.
     */
    EdgeFilter NONE = (n, m) -> false;

    default boolean test(final Edge e) {
        return test(e.getFrom(), e.getTo());
    }
}
