package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Set;

public class PackageCategorizer implements Categorizer {

    @Override
    public Set<Node> apply(final Node node) {
        if (node.hasType(SliceType.classType())) {
            return Set.of(Node.packageNode(packagePart(node.getName())));
        }
        return NONE;
    }

    private String packagePart(final String name) {
        final var lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex >= 0 ? name.substring(0, lastDotIndex) : name;
    }
}
