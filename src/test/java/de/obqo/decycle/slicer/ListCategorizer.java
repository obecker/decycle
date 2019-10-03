package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.List;

/**
 * Represents a list of categories, so for example in {@code ListCategory.of(a, b, c)} the category of {@code a} is
 * {@code b} and the category of {@code b} is {@code c}.
 */
public class ListCategorizer implements Categorizer {

    private List<Node> nodes;

    public static ListCategorizer of(final Node... nodes) {
        return new ListCategorizer(List.of(nodes));
    }

    private ListCategorizer(final List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node apply(final Node node) {
        final int index = this.nodes.indexOf(node);
        return index >= 0 && index < this.nodes.size() - 1 ? this.nodes.get(index + 1) : node;
    }
}
