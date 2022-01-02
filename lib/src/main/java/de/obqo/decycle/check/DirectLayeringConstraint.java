package de.obqo.decycle.check;

import static de.obqo.decycle.model.SliceType.customType;

import de.obqo.decycle.model.Edge;

import java.util.List;

public final class DirectLayeringConstraint extends SlicedConstraint {

    public DirectLayeringConstraint(final String sliceType, final List<Layer> layers) {
        super(customType(sliceType), layers, " => ");
    }

    @Override
    protected boolean isViolatedBy(final Edge edge) {
        final var i = indexOf(edge.getFrom());
        final var j = indexOf(edge.getTo());
        return (containsBothNodes(i, j) && nodesViolateOrder(i, j)) ||
                containsOnlyToNodeWhichIsNotTheFirst(i, j) ||
                containsOnlyFromNodeWhichIsNotTheLast(i, j);
    }

    private boolean nodesViolateOrder(final int i, final int j) {
        return nodesAreInWrongOrder(i, j) || nodesAreNotDirectlyConnected(i, j) || nodesAreInTheSameOneOfLayer(i, j);
    }

    private boolean nodesAreNotDirectlyConnected(final int i, final int j) {
        return j - i > 1;
    }

    private boolean containsOnlyToNodeWhichIsNotTheFirst(final int i, final int j) {
        return i < 0 && j > 0;
    }

    private boolean containsOnlyFromNodeWhichIsNotTheLast(final int i, final int j) {
        return i >= 0 && i < this.layers.size() - 1 && j < 0;
    }
}
