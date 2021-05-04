package de.obqo.decycle.model;

import java.util.function.BiPredicate;

public interface EdgeFilter extends BiPredicate<Node, Node> {

    EdgeFilter ALL = (n, m) -> true;
    EdgeFilter NONE = (n, m) -> false;

    default boolean test(final Edge e) {
        return test(e.getFrom(), e.getTo());
    }
}
