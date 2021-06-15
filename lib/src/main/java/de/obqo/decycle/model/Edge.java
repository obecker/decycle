package de.obqo.decycle.model;

import java.util.Comparator;

import com.google.common.annotations.VisibleForTesting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * An {@link Edge} represents the connection between two {@link Node nodes}.
 * <p>
 * There are two types of edges:
 * <ul>
 * <li>
 * An edge having the {@link #label} {@link EdgeLabel#REFERENCES REFERENCES} represents a dependency
 * from node {@link #from} to node {@link #to}. For example a node for class {@code A} references a node for class
 * {@code B} if {@code A} references (uses, depends on) {@code B}. Similar a package or slice will reference another
 * package or slice if they contain at least one pair of class nodes that reference each other. The {@link #from} and
 * {@link #to} nodes of a {@link EdgeLabel#REFERENCES referencing} edge will always have the same {@link Node#type
 * node type}.
 * </li>
 * <li>
 * An edge having the {@link #label} {@link EdgeLabel#CONTAINS CONTAINS} reflects that node {@link #from} contains node
 * {@link #to}. Typically a package or slice node contains a class node if the class belongs to the package or slice.
 * The {@link #from} and {@link #to} nodes of a {@link EdgeLabel#CONTAINS containing} edge will always have different
 * {@link Node#type node types}.
 * </li>
 * </ul>
 * A {@link EdgeLabel#REFERENCES referencing} edge may have its {@link #ignored} flag set to {@code true}. In this case
 * this edge is not considered when checking any dependency violations between packages or slices.
 */
@Getter
@EqualsAndHashCode(of = { "from", "to", "label" })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Edge implements Comparable<Edge> {

    public static Edge references(final Node from, final Node to, final boolean ignored) {
        return new Edge(from, to, EdgeLabel.REFERENCES, ignored);
    }

    @VisibleForTesting
    public static Edge references(final Node from, final Node to) {
        return new Edge(from, to, EdgeLabel.REFERENCES, false);
    }

    public static Edge contains(final Node from, final Node to) {
        return new Edge(from, to, EdgeLabel.CONTAINS, false);
    }

    public enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    private static final Comparator<Edge> COMPARATOR = Comparator.comparing(Edge::getFrom).thenComparing(Edge::getTo);

    private final Node from;
    private final Node to;
    private final EdgeLabel label;
    private boolean ignored;

    public boolean isReferencing() {
        return this.label == EdgeLabel.REFERENCES;
    }

    public boolean isContaining() {
        return this.label == EdgeLabel.CONTAINS;
    }

    public void ignore(final boolean ignored) {
        this.ignored &= ignored;
    }

    public String displayString() {
        return this.from.displayString() + " → " + this.to.displayString();
    }

    @Override
    public int compareTo(final Edge other) {
        return COMPARATOR.compare(this, other);
    }
}
