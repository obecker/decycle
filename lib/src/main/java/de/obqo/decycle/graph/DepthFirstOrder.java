package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DepthFirstOrder} class represents a data type for determining depth-first search ordering of the nodes in
 * a directed graph, including preorder, postorder, and reverse postorder.
 * <p>
 * This implementation uses depth-first search. Each constructor takes &Theta;(<em>V</em> + <em>E</em>) time, where
 * <em>V</em> is the number of nodes (or vertices) and <em>E</em> is the number of edges. Each instance method takes
 * &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not including the directed graph).
 * <p>
 * This code is based on the
 * <a href="https://algs4.cs.princeton.edu/42digraph/DepthFirstOrder.java.html">DepthFirstOrder</a>
 * implementation from
 * <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
class DepthFirstOrder {

    // contains all nodes that have been marked in depth first search
    private final Set<Node> marked = new HashSet<>();
    // preorder number of a node
    private final Map<Node, Integer> pre = new HashMap<>();
    // postorder number of a node
    private final Map<Node, Integer> post = new HashMap<>();
    // nodes in preorder
    private final List<Node> preorder = new LinkedList<>();
    // nodes in postorder
    private final List<Node> postorder = new LinkedList<>();
    // counter or preorder numbering
    private int preCounter;
    // counter for postorder numbering
    private int postCounter;

    /**
     * Determines a depth-first order for the directed graph {@code g}.
     *
     * @param g the directed graph
     */
    public DepthFirstOrder(final Slicing g) {
        for (final Node node : g.nodes()) {
            if (!this.marked.contains(node)) {
                depthFirstSearch(g, node);
            }
        }
    }

    // run DFS in the directed graph g from node n and compute preorder/postorder
    private void depthFirstSearch(final Slicing g, final Node n) {
        this.marked.add(n);
        this.pre.put(n, this.preCounter++);
        this.preorder.add(n);
        for (final Edge e : g.outEdges(n)) {
            final Node w = e.getTo();
            if (!e.isIgnored() && !this.marked.contains(w)) {
                depthFirstSearch(g, w);
            }
        }
        this.postorder.add(n);
        this.post.put(n, this.postCounter++);
    }

    /**
     * Returns the preorder number of node {@code n}.
     *
     * @param n the node
     * @return the preorder number of node {@code n}
     * @throws IllegalArgumentException if n doesn't belong to the graph
     */
    public int pre(final Node n) {
        validateNode(n);
        return this.pre.get(n);
    }

    /**
     * Returns the postorder number of node {@code n}.
     *
     * @param n the node
     * @return the postorder number of node {@code n}
     * @throws IllegalArgumentException if n doesn't belong to the graph
     */
    public int post(final Node n) {
        validateNode(n);
        return this.post.get(n);
    }

    /**
     * Returns the nodes in postorder.
     *
     * @return the nodes in postorder, as an iterable of nodes
     */
    public Iterable<Node> post() {
        return this.postorder;
    }

    /**
     * Returns the nodes in preorder.
     *
     * @return the nodes in preorder, as an iterable of nodes
     */
    public Iterable<Node> pre() {
        return this.preorder;
    }

    /**
     * Returns the nodes in reverse postorder.
     *
     * @return the nodes in reverse postorder
     */
    public List<Node> reversePost() {
        final List<Node> reverse = new ArrayList<>(this.postorder);
        Collections.reverse(reverse);
        return reverse;
    }

    // throw an IllegalArgumentException if n doesn't belong to the graph
    private void validateNode(final Node n) {
        if (!this.marked.contains(n)) {
            throw new IllegalArgumentException("node " + n + " doesn't belong to the graph");
        }
    }
}
