package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Edge.EdgeLabel.CONTAINS;
import static de.obqo.decycle.model.Edge.EdgeLabel.REFERENCES;
import static java.util.Objects.requireNonNullElse;
import static java.util.function.Predicate.not;

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
import com.google.common.graph.NetworkBuilder;

@SuppressWarnings("UnstableApiUsage")
public class Graph implements SliceSource {

    private final Categorizer categorizer;
    private final NodeFilter filter;
    private final BiPredicate<Node, Node> edgeFilter;
    private final EdgeFilter ignoredEdgesFilter;

    private final MutableNetwork<Node, Edge> internalGraph = NetworkBuilder.directed().build();

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
        addReference(a, b);
        add(a);
        add(b);
    }

    private void addReference(final Node a, final Node b) {
        if (this.filter.test(a) && this.filter.test(b) && this.edgeFilter.test(a, b)) {
            final boolean ignored = this.ignoredEdgesFilter.test(a, b);
            this.internalGraph.addEdge(a, b, Edge.references(a, b, ignored));
        }
    }

    void add(final Node node) {
        if (this.filter.test(node)) {
            addNodeToSlices(node);
        }
    }

    private void addNodeToSlices(final Node node) {
        final var categories = this.categorizer.apply(node);
        if (categories.isEmpty()) {
            this.internalGraph.addNode(node);
        } else {
            categories.forEach(category -> {
                if (!category.equals(node)) {
                    this.internalGraph.addEdge(category, node, Edge.contains(category, node));
                    addNodeToSlices(category);
                }
            });
        }
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
    public Set<String> sliceTypes() {
        return this.internalGraph.nodes().stream().map(Node::getType).collect(Collectors.toSet());
    }

    @Override
    public Slice slice(final String name) {
        final var slice = MutableSlice.create(name);

        final var sliceNodeFinder = new SliceNodeFinder(name, this.internalGraph);

        this.internalGraph.nodes().stream()
                .filter(n -> n.hasType(name))
                .forEach(slice::addNode);

        this.internalGraph.edges().stream()
                .filter(Edge::isReferencing)
                .forEach(edge -> sliceNodeFinder.find(edge.getFrom()).ifPresent(n1 ->
                        sliceNodeFinder.find(edge.getTo()).filter(not(n1::equals)).ifPresent(n2 ->
                                slice.edgeConnecting(n1, n2).ifPresentOrElse(
                                        // if present, set/toggle ignored flag
                                        slideEdge -> slideEdge.ignore(edge.isIgnored()),
                                        // else add new slice edge
                                        () -> slice.addEdge(Edge.references(n1, n2, edge.isIgnored()))))));

        return slice;
    }

    public Set<Edge> containingClassEdges(final Edge edge) {
        if (!edge.isReferencing()) {
            return Set.of();
        }
        final Stream<Node> containingFromNodes = containingClassNodes(edge.getFrom());
        final Set<Node> containingToNodes = containingClassNodes(edge.getTo()).collect(Collectors.toSet());

        final Set<Edge> containingEdges = new HashSet<>();
        containingFromNodes.forEach(from ->
                containingToNodes.forEach(to ->
                        this.internalGraph.edgeConnecting(from, to).ifPresent(containingEdges::add)));
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
