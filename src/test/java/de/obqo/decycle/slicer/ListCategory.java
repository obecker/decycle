package de.obqo.decycle.slicer;

import java.util.List;

import de.obqo.decycle.model.Node;

/**
 * Represents a list of categories, so for example in {@code ListCategory.of(a, b, c)} the category of {@code a} is
 * {@code b} and the category of {@code b} is {@code c}.
 */
public class ListCategory implements Categorizer {

    private List<Node> nodes;

    public static ListCategory of(final Node... nodes) {
        return new ListCategory(List.of(nodes));
    }

    private ListCategory(final List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node apply(final Node node) {
        final int index = this.nodes.indexOf(node);
        return index >= 0 && index < this.nodes.size() - 1 ? this.nodes.get(index + 1) : node;
    }
}
