package de.obqo.decycle.analysis;

import java.util.Objects;
import java.util.function.BiPredicate;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.Categorizer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoSelfReference implements BiPredicate<Node, Node> {

    private final Categorizer categorizer;

    public NoSelfReference() {
        this(n -> n);
    }

    @Override
    public boolean test(final Node node1, final Node node2) {
        return !(findInCategory(node1, node2) || findInCategory(node2, node1));
    }

    private boolean findInCategory(final Node a, final Node b) {
        final var next = this.categorizer.apply(a);
        return Objects.equals(a, b) || (!Objects.equals(next, a) && findInCategory(next, b));
    }
}
