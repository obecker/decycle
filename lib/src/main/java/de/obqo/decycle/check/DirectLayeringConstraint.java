package de.obqo.decycle.check;

import static de.obqo.decycle.model.SliceType.customType;

import de.obqo.decycle.model.Node;

import java.util.List;
import java.util.Objects;

public class DirectLayeringConstraint extends SlicedConstraint {

    public DirectLayeringConstraint(final String sliceType, final List<Layer> layers) {
        super(customType(sliceType), layers, " => ");
    }

    @Override
    boolean isViolatedBy(final Node n1, final Node n2) {
        final var i1 = indexOf(n1);
        final var i2 = indexOf(n2);
        return (constraintContainsBothNodes(i1, i2) &&
                (i1 > i2 ||
                        i2 - i1 > 1 ||
                        (!Objects.equals(n1, n2) && i1 == i2 && this.layers.get(i1).denyDependenciesWithinLayer()))) ||
                (i1 < 0 && i2 > 0) ||
                (i1 >= 0 && i1 < this.layers.size() - 1 && i2 < 0);
    }
}
