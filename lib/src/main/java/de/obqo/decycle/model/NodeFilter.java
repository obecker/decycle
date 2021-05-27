package de.obqo.decycle.model;

import java.util.function.Predicate;

public interface NodeFilter extends Predicate<Node> {

    /**
     * A filter that matches all nodes
     */
    NodeFilter ALL = node -> true;
}
