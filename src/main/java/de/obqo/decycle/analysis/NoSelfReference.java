package de.obqo.decycle.analysis;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.Categorizer;

import java.util.Objects;
import java.util.function.BiPredicate;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoSelfReference implements BiPredicate<Node, Node> {

    private final Categorizer categorizer;

    @VisibleForTesting
    NoSelfReference() {
        this(__ -> Categorizer.NONE);
    }

    @Override
    public boolean test(final Node node1, final Node node2) {
        return !(findInCategory(node1, node2) || findInCategory(node2, node1));
    }

    private boolean findInCategory(final Node a, final Node b) {
        return Objects.equals(a, b) || this.categorizer.apply(a).stream().anyMatch(node -> findInCategory(node, b));
    }
}
