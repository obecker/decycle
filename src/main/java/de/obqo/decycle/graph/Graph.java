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
import lombok.RequiredArgsConstructor;
import lombok.val;

public class Graph {

    private enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class Edge {

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

    public void connect(Node a, Node b) {
        System.out.println(String.format("%s -> %s", a, b));

        addEdge(a, b);
        add(a);
        add(b);
    }

    private void addEdge(final Node a, final Node b) {
        if (filter.test(a) && filter.test(b) && edgeFilter.test(a, b)) {
            internalGraph.addEdge(a, b, new Edge(a, b, EdgeLabel.REFERENCES));
        }
    }

    public void add(Node node) {
        System.out.println(String.format("Add %s", node));

        if (filter.test(node)) {
            unfilteredAdd(node);
        }
    }

    private void unfilteredAdd(Node node) {
        val cat = category.apply(node);
        if (cat.equals(node)) {
            internalGraph.addNode(node);
        } else {
            addNodeToSlice(node, cat);
            unfilteredAdd(cat);
        }
    }

    private void addNodeToSlice(final Node node, final Node cat) {
        internalGraph.addEdge(cat, node, new Edge(cat, node, EdgeLabel.CONTAINS));
    }

    public Set<Node> allNodes() {
        return internalGraph.nodes();
    }

    public Set<Node> topNodes() {
        return internalGraph.nodes().stream()
                            .filter(n -> internalGraph.inEdges(n).stream()
                                                      .allMatch(e -> e.label != EdgeLabel.CONTAINS))
                            .collect(Collectors.toSet());
    }

    private Set<Node> connectedNodes(Node node, EdgeLabel label) {
        return internalGraph.nodes().contains(node) ? internalGraph.outEdges(node).stream()
                                                                   .filter(e -> e.label == label)
                                                                   .map(e -> e.to)
                                                                   .collect(Collectors.toSet()) : Set.of();
    }

    public Set<Node> contentsOf(Node group) {
        return connectedNodes(group, EdgeLabel.CONTAINS);
    }

    public Set<Node> connectionsOf(Node node) {
        return connectedNodes(node, EdgeLabel.REFERENCES);
    }
}
