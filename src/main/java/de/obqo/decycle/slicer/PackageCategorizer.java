package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

public class PackageCategorizer implements Categorizer {

    @Override
    public Node apply(final Node node) {
        if (node instanceof SimpleNode && node.getTypes().contains(SimpleNode.CLASS)) {
            return SimpleNode.packageNode(packagePart(node.getName()));
        }
        return node;
    }

    private String packagePart(final String name) {
        final var lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex >= 0 ? name.substring(0, lastDotIndex) : name;
    }
}
