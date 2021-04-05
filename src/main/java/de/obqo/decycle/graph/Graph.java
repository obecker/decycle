package de.obqo.decycle.graph;

import static de.obqo.decycle.graph.Edge.EdgeLabel.CONTAINS;
import static de.obqo.decycle.graph.Edge.EdgeLabel.REFERENCES;
import static de.obqo.decycle.util.ObjectUtils.defaultValue;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.NodeFilter;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

public class Graph implements SliceSource {

    private final Categorizer categorizer;
    private final NodeFilter filter;
    private final BiPredicate<Node, Node> edgeFilter;

    private final MutableNetwork<Node, Edge> internalGraph =
            NetworkBuilder.directed().allowsParallelEdges(true).build();

    public Graph() {
        this(null);
    }

    public Graph(final Categorizer categorizer) {
        this(categorizer, null);
    }

    public Graph(final Categorizer categorizer, final NodeFilter filter) {
        this(categorizer, filter, null);
    }

    public Graph(final Categorizer categorizer, final NodeFilter filter,
            final BiPredicate<Node, Node> edgeFilter) {
        this.categorizer = defaultValue(categorizer, __ -> Categorizer.NONE);
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
        if (cat.isEmpty()) {
            this.internalGraph.addNode(node);
        } else {
            cat.forEach(category -> {
                if (!category.equals(node)) {
                    addNodeToSlice(node, category);
                    unfilteredAdd(category);
                }
            });
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
        return this.internalGraph.nodes().stream().map(Node::getType).collect(Collectors.toSet());
    }

    @Override
    public Network<Node, Edge> slice(final String name) {

        final var sliceNodes = this.internalGraph.nodes().stream()
                .filter(n -> n.hasType(name))
                .collect(Collectors.toSet());

        final var sliceNodeFinder = new SliceNodeFinder(name, this.internalGraph);

        final var sliceGraph = NetworkBuilder.directed().allowsParallelEdges(true).<Node, Edge>build();
        sliceNodes.forEach(sliceGraph::addNode);

        final var edges = this.internalGraph.edges().stream()
                .filter(e -> e.getLabel() == REFERENCES)
                .collect(Collectors.toSet());

        for (final Edge edge : edges) {
            sliceNodeFinder.find(edge.getFrom()).ifPresent(n1 ->
                    sliceNodeFinder.find(edge.getTo()).ifPresent(n2 -> {
                        if (!Objects.equals(n1, n2)) {
                            sliceGraph.addEdge(n1, n2, new Edge(n1, n2, REFERENCES));
                        }
                    }));
        }

        return sliceGraph;
    }
}
