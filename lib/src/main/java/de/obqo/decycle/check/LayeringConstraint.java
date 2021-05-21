package de.obqo.decycle.check;

import de.obqo.decycle.model.Node;

import java.util.List;
import java.util.Objects;

public class LayeringConstraint extends SlicedConstraint {

    public LayeringConstraint(final String sliceType, final List<Layer> layers) {
        super(sliceType, layers, " -> ");
    }

    @Override
    boolean isViolatedBy(final Node n1, final Node n2) {
        final var i1 = indexOf(n1);
        final var i2 = indexOf(n2);
        return constraintContainsBothNodes(i1, i2) &&
                (i1 > i2 ||
                        !Objects.equals(n1, n2) && i1 == i2 && this.layers.get(i1).denyDependenciesWithinLayer());
    }
}
