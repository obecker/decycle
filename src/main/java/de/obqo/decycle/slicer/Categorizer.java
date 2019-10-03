package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.function.UnaryOperator;

public interface Categorizer extends UnaryOperator<Node> {

    default Categorizer combine(final Categorizer other) {
        return node -> other.apply(apply(node));
    }
}
