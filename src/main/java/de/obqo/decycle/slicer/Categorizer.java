package de.obqo.decycle.slicer;

import java.util.function.UnaryOperator;

import de.obqo.decycle.model.Node;

public interface Categorizer extends UnaryOperator<Node> {

    default Categorizer combine(final Categorizer other) {
        return node -> other.apply(apply(node));
    }
}
