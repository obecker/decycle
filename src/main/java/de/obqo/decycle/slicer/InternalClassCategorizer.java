package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.Set;

public class InternalClassCategorizer implements Categorizer {

    @Override
    public Set<Node> apply(final Node node) {
        if (node.hasType(Node.CLASS) && node.getName().contains("$")) {
            return Set.of(Node.classNode(node.getName().split("\\$")[0]));
        }
        return NONE;
    }
}
