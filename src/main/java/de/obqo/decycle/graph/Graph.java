package de.obqo.decycle.graph;

import static de.obqo.decycle.graph.Edge.EdgeLabel.CONTAINS;
import static de.obqo.decycle.graph.Edge.EdgeLabel.REFERENCES;
import static de.obqo.decycle.util.ObjectUtils.defaultValue;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;
import de.obqo.decycle.slicer.Categorizer;

public class Graph implements SliceSource {

    private final Categorizer categorizer;
    private final Predicate<Node> filter;
    private final BiPredicate<Node, Node> edgeFilter;

    private final MutableNetwork<Node, Edge> internalGraph =
            NetworkBuilder.directed().allowsParallelEdges(true).build();

    public Graph() {
        this(null);
    }

    public Graph(final Categorizer categorizer) {
        this(categorizer, null);
    }

    public Graph(final Categorizer categorizer, final Predicate<Node> filter) {
        this(categorizer, filter, null);
    }

    public Graph(final Categorizer categorizer, final Predicate<Node> filter,
                 final BiPredicate<Node, Node> edgeFilter) {
        this.categorizer = defaultValue(categorizer, n -> n);
        this.filter = defaultValue(filter, __ -> true);
        this.edgeFilter = defaultValue(edgeFilter, (n, m) -> !Objects.equals(n, m));
    }

    public void connect(final Node a, final Node b) {
        addEdge(a, b);
        add(a);
        add(b);
    }

    private void addEdge(final Node a, final Node b) {
        if (this.filter.test(a) && this.filter.test(b) && this.edgeFilter.test(a, b)) {
            this.internalGraph.addEdge(a, b, new Edge(a, b, REFERENCES));
        }
    }

    public void add(final Node node) {
        if (this.filter.test(node)) {
            unfilteredAdd(node);
        }
    }

    private void unfilteredAdd(final Node node) {
        final var cat = this.categorizer.apply(node);
        if (cat.equals(node)) {
            this.internalGraph.addNode(node);
        } else {
            addNodeToSlice(node, cat);
            unfilteredAdd(cat);
        }
    }

    private void addNodeToSlice(final Node node, final Node cat) {
        this.internalGraph.addEdge(cat, node, new Edge(cat, node, CONTAINS));
    }

    public Set<Node> allNodes() {
        return this.internalGraph.nodes();
    }

    public Set<Node> topNodes() {
        return this.internalGraph.nodes().stream()
                                 .filter(n -> this.internalGraph.inEdges(n).stream()
                                                                .allMatch(e -> e.getLabel() != CONTAINS))
                                 .collect(Collectors.toSet());
    }

    private Set<Edge> outEdges(final Node node) {
        return this.internalGraph.nodes().contains(node) ? this.internalGraph.outEdges(node) : Set.of();
    }

    private Set<Node> connectedNodes(final Node node, final Edge.EdgeLabel label) {
        return outEdges(node).stream()
                             .filter(e -> e.getLabel() == label)
                             .map(Edge::getTo)
                             .collect(Collectors.toSet());
    }

    public Set<Node> contentsOf(final Node group) {
        return connectedNodes(group, CONTAINS);
    }

    public Set<Node> connectionsOf(final Node node) {
        return connectedNodes(node, REFERENCES);
    }

    @Override
    public Set<String> slices() {
        return this.internalGraph.nodes().stream().flatMap(n -> n.getTypes().stream()).collect(Collectors.toSet());
    }

    @Override
    public Network<Node, Edge> slice(final String name) {

        final var sliceNodes = this.internalGraph.nodes().stream()
                                                 .filter(n -> n instanceof SimpleNode && n.getTypes().contains(name))
                                                 .collect(Collectors.toSet());

        final var sliceNodeFinder = new SliceNodeFinder(name, this.internalGraph);

        final var sliceGraph = NetworkBuilder.directed().allowsParallelEdges(true).<Node, Edge>build();
        sliceNodes.forEach(sliceGraph::addNode);

        //---------------
        final var edges = this.internalGraph.edges().stream()
                                            .filter(e -> e.getLabel() == REFERENCES)
                                            .collect(Collectors.toSet());

        for (final Edge edge : edges) {
            final var s1 = sliceNodeFinder.lift(edge.getFrom());
            final var s2 = sliceNodeFinder.lift(edge.getTo());
            s1.ifPresent(n1 ->
                    s2.ifPresent(n2 ->
                            sliceGraph.addEdge(n1, n2, new Edge(n1, n2, REFERENCES))));
        }

        return sliceGraph;
    }
}
