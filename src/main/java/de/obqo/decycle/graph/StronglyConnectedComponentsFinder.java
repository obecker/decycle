package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StronglyConnectedComponentsFinder {

    /**
     * Find the edges of all
     * <a href="https://en.wikipedia.org/wiki/Strongly_connected_component">strongly connected components</a>
     * (SCC) of the given directed {@code graph}. For a cycle-free graph this method returns an empty set.
     *
     * @param graph the graph
     * @return a set of all SCCs, each SCC represented as a set of {@link Edge}s
     */
    public static Set<Set<Edge>> findComponents(final Slice graph) {
        final Set<Set<Node>> components = new TarjansAlgorithm(graph).getMultiNodeComponents();

        final Set<Set<Edge>> result = new HashSet<>();
        for (final Set<Node> component : components) {
            final Set<Edge> edges = new HashSet<>();
            for (final Node from : component) {
                for (final Node to : component) {
                    graph.edgeConnecting(from, to).ifPresent(edges::add);
                }
            }
            result.add(edges);
        }
        return result;
    }

    /**
     * The {@code TarjansAlgorithm} class represents a data type for determining the strong components in a directed
     * graph. With {@link #getMultiNodeComponents()} it will return only those components that contain more than one
     * node.
     *
     * <p>
     * This implementation uses Tarjan's algorithm. The constructor takes time proportional to <em>N</em> + <em>E</em>
     * (in the worst case), where <em>N</em> is the number of nodes (or vertices) and <em>E</em> is the number of edges.
     * Accessing the resulting components takes constant time.
     * <p>
     * This code is based on the
     * <a href="https://algs4.cs.princeton.edu/42digraph/TarjanSCC.java.html">TarjanSCC</a>
     * implementation from
     * <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
     * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
     *
     * @author Robert Sedgewick
     * @author Kevin Wayne
     */
    private static class TarjansAlgorithm {

        /**
         * The directed graph
         */
        private final Slice graph;

        /**
         * Number of nodes in the graph
         */
        private final int nodeCount;

        /**
         * marked.contains(node) = has node been visited
         */
        private final Set<Node> marked = new HashSet<>();

        /**
         * id.get(node) = id of strong component containing node
         */
        private final Map<Node, Integer> id = new HashMap<>();

        /**
         * low.get(node) = low number of node
         */
        private final Map<Node, Integer> low = new HashMap<>();

        /**
         * preorder number counter
         */
        private int pre;

        /**
         * Number of strongly-connected components
         */
        private int count;

        /**
         * Set of the detected strongly-connected components with size > 1
         */
        private final Set<Set<Node>> multiNodeComponents;

        /**
         * Computes the strong components of the directed graph {@code grapg}.
         *
         * @param graph the directed graph
         */
        TarjansAlgorithm(final Slice graph) {
            this.graph = graph;
            final Set<Node> nodes = graph.nodes();
            this.nodeCount = nodes.size();

            final Map<Integer, Set<Node>> componentMap = new HashMap<>();
            for (final Node node : nodes) {
                if (!this.marked.contains(node)) {
                    depthFirstSearch(node, new LinkedList<>());
                }
                final Set<Node> componentNodes = componentMap.computeIfAbsent(this.id.get(node), HashSet::new);
                componentNodes.add(node);
            }

            this.multiNodeComponents = componentMap.values().stream()
                    .filter(set -> set.size() > 1)
                    .collect(Collectors.toUnmodifiableSet());
        }

        private void depthFirstSearch(final Node node, final Deque<Node> stack) {
            this.marked.add(node);
            int min = this.pre++;
            this.low.put(node, min);
            stack.push(node);
            for (final Edge edge : this.graph.outEdges(node)) {
                if (!edge.isIgnored()) {
                    final Node next = edge.getTo();
                    if (!this.marked.contains(next)) {
                        depthFirstSearch(next, stack);
                    }
                    min = Math.min(this.low.get(next), min);
                }
            }
            if (min < this.low.get(node)) {
                this.low.put(node, min);
                return;
            }

            Node n;
            do {
                n = stack.pop();
                this.id.put(n, this.count);
                this.low.put(n, this.nodeCount);
            } while (!Objects.equals(n, node));
            this.count++;
        }

        Set<Set<Node>> getMultiNodeComponents() {
            return this.multiNodeComponents;
        }
    }
}
