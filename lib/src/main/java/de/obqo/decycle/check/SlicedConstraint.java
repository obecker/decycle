package de.obqo.decycle.check;

import static java.util.function.Predicate.not;

import de.obqo.decycle.graph.MutableSlicing;
import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link SlicedConstraint} represents some dependency rules for the slices of a {@link
 * de.obqo.decycle.graph.Slicing}. Such constraint is defined by a list of {@link Layer layers}, each containing a set
 * of slices (i.e. a set of slice node names). The actual dependencies in a {@link de.obqo.decycle.graph.Slicing} must
 * match the dependencies defined by this constraint and its layers.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SlicedConstraint implements Constraint {

    private final SliceType sliceType;
    protected final List<Layer> layers;
    private final String arrow;

    protected abstract boolean isViolatedBy(Edge edge);

    protected final int indexOf(final Node node) {
        for (int i = 0; i < this.layers.size(); i++) {
            if (this.layers.get(i).contains(node.getName())) {
                return i;
            }
        }
        return -1;
    }

    protected final boolean containsBothNodes(final int i, final int j) {
        return i >= 0 && j >= 0;
    }

    protected final boolean nodesAreInWrongOrder(final int i, final int j) {
        return i > j;
    }

    protected final boolean nodesAreInTheSameOneOfLayer(final int i, final int j) {
        return i == j && this.layers.get(i).denyDependenciesWithinLayer();
    }

    @Override
    public List<Violation> violations(final SlicingSource slicingSource) {
        final var slicing = slicingSource.slicing(this.sliceType);
        validateConstraint(slicing);
        final var violatingDeps = slicing.edges().stream()
                .filter(not(Edge::isIgnored))
                .filter(this::isViolatedBy)
                .collect(Collectors.toSet());
        return violatingDeps.isEmpty() ? List.of()
                : List.of(new Violation(getShortString(), MutableSlicing.create(this.sliceType, violatingDeps)));
    }

    private void validateConstraint(final Slicing slicing) {
        final Set<String> sliceNames = slicing.nodes().stream().map(Node::getName).collect(Collectors.toSet());
        final String unknownSlices = this.layers.stream()
                .map(Layer::getSlices)
                .flatMap(Collection::stream)
                .filter(not(sliceNames::contains))
                .sorted()
                .collect(Collectors.joining(", "));
        if (!unknownSlices.isEmpty()) {
            final var sliceText = unknownSlices.contains(",") ? "slices" : "slice";
            log.warn("Decycle: Unknown {} {} in constraint '{}' for slicing {}",
                    sliceText, unknownSlices, getShortString(), this.sliceType.displayString());
        }
    }

    @Override
    public String getShortString() {
        return this.layers.stream().map(Layer::getShortString).collect(Collectors.joining(this.arrow));
    }

    @Override
    public String toString() {
        return getShortString();
    }
}
