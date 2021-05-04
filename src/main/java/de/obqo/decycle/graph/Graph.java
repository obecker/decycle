package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Edge.EdgeLabel.CONTAINS;
import static de.obqo.decycle.model.Edge.EdgeLabel.REFERENCES;
import static java.util.Objects.requireNonNullElse;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.Categorizer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

public class Graph implements SliceSource {

    private final Categorizer categorizer;
    private final NodeFilter filter;
    private final BiPredicate<Node, Node> edgeFilter;
    private final EdgeFilter ignoredEdgesFilter;

    private final MutableNetwork<Node, Edge> internalGraph =
            NetworkBuilder.directed().allowsParallelEdges(true).build();

    public Graph() {
        this(null);
    }

    public Graph(final Categorizer categorizer) {
        this(categorizer, null);
    }

    public Graph(final Categorizer categorizer, final NodeFilter filter) {
        this(categorizer, filter, null, null);
    }

    public Graph(final Categorizer categorizer, final NodeFilter filter,
            final EdgeFilter edgeFilter, final EdgeFilter ignoredEdgesFilter) {
        this.categorizer = requireNonNullElse(categorizer, Categorizer.EMPTY);
        this.filter = requireNonNullElse(filter, NodeFilter.ALL);
        this.edgeFilter = requireNonNullElse(edgeFilter, (n, m) -> !Objects.equals(n, m));
        this.ignoredEdgesFilter = requireNonNullElse(ignoredEdgesFilter, EdgeFilter.NONE);
    }

    public void connect(final Node a, final Node b) {
        addEdge(a, b);
        add(a);
        add(b);
    }

    private void addEdge(final Node a, final Node b) {
        if (this.filter.test(a) && this.filter.test(b) && this.edgeFilter.test(a, b)) {
            final boolean ignored = this.ignoredEdgesFilter.test(a, b);
            this.internalGraph.addEdge(a, b, Edge.references(a, b, ignored));
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
        this.internalGraph.addEdge(cat, node, Edge.contains(cat, node));
    }

    public Set<Node> allNodes() {
        return this.internalGraph.nodes();
    }

    public Set<Node> topNodes() {
        return this.internalGraph.nodes().stream()
                .filter(n -> this.internalGraph.inEdges(n).stream()
                        .allMatch(Edge::isReferencing))
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

        final var sliceGraph = NetworkBuilder.directed().allowsParallelEdges(true).<Node, Edge>build();
        sliceNodes.forEach(sliceGraph::addNode);

        final var edges = this.internalGraph.edges().stream()
                .filter(e -> e.getLabel() == REFERENCES)
                .collect(Collectors.toSet());

        final var sliceNodeFinder = new SliceNodeFinder(name, this.internalGraph);
        for (final Edge edge : edges) {
            sliceNodeFinder.find(edge.getFrom()).ifPresent(n1 ->
                    sliceNodeFinder.find(edge.getTo()).ifPresent(n2 -> {
                        if (!Objects.equals(n1, n2)) {
                            sliceGraph.edgeConnecting(n1, n2).ifPresentOrElse(
                                    // if present, set/toggle ignored flag
                                    slideEdge -> slideEdge.ignore(edge.isIgnored()),
                                    // else add new slice edge
                                    () -> sliceGraph.addEdge(n1, n2, Edge.references(n1, n2, edge.isIgnored())));
                        }
                    }));
        }

        return sliceGraph;
    }

    public Set<Edge> containingClassEdges(final Edge edge) {
        if (edge.getLabel() != REFERENCES) {
            return Set.of();
        }
        final Stream<Node> containingFromNodes = containingClassNodes(edge.getFrom());
        final Set<Node> containingToNodes = containingClassNodes(edge.getTo()).collect(Collectors.toSet());

        final Set<Edge> containingEdges = new HashSet<>();
        containingFromNodes.forEach(from -> {
            containingToNodes.forEach(to -> {
                this.internalGraph.edgeConnecting(from, to).ifPresent(containingEdges::add);
            });
        });
        return containingEdges;
    }

    private Stream<Node> containingClassNodes(final Node node) {
        final Stream<Node> nodes = node.getType().equals(Node.CLASS) ? Stream.of(node) : Stream.empty();
        // TODO check: this further recursion for CLASS nodes is needed because of inner classes
        // Do we really need the InternalClassCategorizer?

        return Stream.concat(nodes, this.internalGraph.outEdges(node)
                .stream().filter(Edge::isContaining)
                .map(Edge::getTo)
                .flatMap(this::containingClassNodes));
    }
}
