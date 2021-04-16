package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import java.util.Comparator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Edge implements Comparable<Edge> {

    public static Edge references(final Node from, final Node to) {
        return new Edge(from, to, EdgeLabel.REFERENCES);
    }

    public static Edge contains(final Node from, final Node to) {
        return new Edge(from, to, EdgeLabel.CONTAINS);
    }

    enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    private static final Comparator<Edge> COMPARATOR = Comparator.comparing(Edge::getFrom).thenComparing(Edge::getTo);

    private final Node from;
    private final Node to;
    private final EdgeLabel label;

    public boolean isReferencing() {
        return this.label == EdgeLabel.REFERENCES;
    }

    public boolean isContaining() {
        return this.label == EdgeLabel.CONTAINS;
    }

    @Override
    public int compareTo(final Edge other) {
        return COMPARATOR.compare(this, other);
    }
}
