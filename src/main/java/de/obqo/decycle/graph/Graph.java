package de.obqo.decycle.graph;

import static de.obqo.decycle.util.ObjectUtils.defaultValue;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.obqo.decycle.model.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Graph {

    enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Getter
    static class Edge {

        private final Node from;
        private final Node to;
        private final EdgeLabel label;
    }

    private final Function<Node, Node> category;
    private final Predicate<Node> filter;
    private final BiPredicate<Node, Node> edgeFilter;

    public Graph() {
        this(null);
    }

    public Graph(final Function<Node, Node> category) {
        this(category, null);
    }

    public Graph(final Function<Node, Node> category, final Predicate<Node> filter) {
        this(category, filter, null);
    }

    public Graph(final Function<Node, Node> category, final Predicate<Node> filter,
                 final BiPredicate<Node, Node> edgeFilter) {
        this.category = defaultValue(category, n -> n);
        this.filter = defaultValue(filter, __ -> true);
        this.edgeFilter = defaultValue(edgeFilter, (n, m) -> true).and((n, m) -> !Objects.equals(n, m));
    }

    private MutableNetwork<Node, Edge> internalGraph =
            NetworkBuilder.directed().allowsParallelEdges(true).build();

    public void connect(final Node a, final Node b) {
        System.out.println(String.format("%s -> %s", a, b));

        addEdge(a, b);
        add(a);
        add(b);
    }

    private void addEdge(final Node a, final Node b) {
        if (this.filter.test(a) && this.filter.test(b) && this.edgeFilter.test(a, b)) {
            this.internalGraph.addEdge(a, b, new Edge(a, b, EdgeLabel.REFERENCES));
        }
    }

    public void add(final Node node) {
        System.out.println(String.format("Add %s", node));

        if (this.filter.test(node)) {
            unfilteredAdd(node);
        }
    }

    private void unfilteredAdd(final Node node) {
        final var cat = this.category.apply(node);
        if (cat.equals(node)) {
            this.internalGraph.addNode(node);
        } else {
            addNodeToSlice(node, cat);
            unfilteredAdd(cat);
        }
    }

    private void addNodeToSlice(final Node node, final Node cat) {
        this.internalGraph.addEdge(cat, node, new Edge(cat, node, EdgeLabel.CONTAINS));
    }

    public Set<Node> allNodes() {
        return this.internalGraph.nodes();
    }

    public Set<Node> topNodes() {
        return this.internalGraph.nodes().stream()
                                 .filter(n -> this.internalGraph.inEdges(n).stream()
                                                                .allMatch(e -> e.label != EdgeLabel.CONTAINS))
                                 .collect(Collectors.toSet());
    }

    private Set<Node> connectedNodes(final Node node, final EdgeLabel label) {
        return this.internalGraph.nodes().contains(node)
                ? this.internalGraph.outEdges(node).stream()
                                    .filter(e -> e.label == label)
                                    .map(e -> e.to)
                                    .collect(Collectors.toSet())
                : Set.of();
    }

    public Set<Node> contentsOf(final Node group) {
        return connectedNodes(group, EdgeLabel.CONTAINS);
    }

    public Set<Node> connectionsOf(final Node node) {
        return connectedNodes(node, EdgeLabel.REFERENCES);
    }
}
