package de.obqo.decycle.check;

import static de.obqo.decycle.model.SliceType.customType;

import de.obqo.decycle.model.Edge;

import java.util.List;

public final class LayeringConstraint extends SlicedConstraint {

    public LayeringConstraint(final String sliceType, final List<Layer> layers) {
        super(customType(sliceType), layers, " -> ");
    }

    @Override
    protected boolean isViolatedBy(final Edge edge) {
        final var i = indexOf(edge.getFrom());
        final var j = indexOf(edge.getTo());
        return containsBothNodes(i, j) && nodesViolateOrder(i, j);
    }

    private boolean nodesViolateOrder(final int i, final int j) {
        return nodesAreInWrongOrder(i, j) || nodesAreInTheSameOneOfLayer(i, j);
    }
}
