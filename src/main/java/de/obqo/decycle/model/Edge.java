package de.obqo.decycle.model;

import java.util.Comparator;

import com.google.common.annotations.VisibleForTesting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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

    @Override
    public int compareTo(final Edge other) {
        return COMPARATOR.compare(this, other);
    }
}
