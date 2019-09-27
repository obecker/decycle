package de.obqo.decycle.slicer;

import java.util.List;
import java.util.function.Function;

import de.obqo.decycle.model.Node;

/**
 * Represents a list of categories, so for example in {@code ListCategory.of(a, b, c)} the category of {@code a} is
 * {@code b} and the category of {@code b} is {@code c}.
 */
public class ListCategory implements Function<Node, Node> {

    private List<Node> nodes;

    public static ListCategory of(Node... nodes) {
        return new ListCategory(List.of(nodes));
    }

    private ListCategory(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node apply(final Node node) {
        int index = nodes.indexOf(node);
        return index >= 0 && index < nodes.size() - 1 ? nodes.get(index + 1) : node;
    }
}
