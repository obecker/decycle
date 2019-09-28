package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

public class InternalClassCategorizer implements Categorizer {

    @Override
    public Node apply(final Node node) {
        if (node instanceof SimpleNode && node.getTypes().contains(SimpleNode.CLASS) && node.getName().contains("$")) {
            return SimpleNode.classNode(node.getName().split("\\$")[0]);
        }
        return node;
    }
}
