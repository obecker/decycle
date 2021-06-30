package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Topological} class represents a data type for determining a topological order of a <em>directed acyclic
 * graph</em> (DAG). A directed graph has a topological order if and only if it is a DAG. If the graph is not acyclic
 * the returned order is not a topological order - this implementation doesn't check for cycles.
 * <p>
 * This implementation uses depth-first search. The constructor takes &Theta;(<em>V</em> + <em>E</em>) time in the worst
 * case, where <em>V</em> is the number of nodes (or vertices) and <em>E</em> is the number of edges. Each instance
 * method takes &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not including the directed graph).
 * <p>
 * This code is based on the
 * <a href="https://algs4.cs.princeton.edu/42digraph/Topological.java.html">Topological</a> implementation from
 * <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
class Topological {

    private List<Node> order;  // topological order
    private Map<Node, Integer> rank;

    /**
     * Determines a topological order of the directed graph {@code g}.
     *
     * @param g the directed graph
     */
    public Topological(final Slicing g) {
        final DepthFirstOrder dfs = new DepthFirstOrder(g);
        this.order = dfs.reversePost();
        this.rank = new HashMap<>();
        int i = 0;
        for (final Node n : this.order) {
            this.rank.put(n, i++);
        }
    }

    /**
     * Returns a topological order if the directed graph has a topologial order, and some other order otherwise.
     *
     * @return the nodes in topological order if the directed graph has a topological order (or equivalently, if the
     * digraph is a DAG), and some other order otherwise
     */
    public List<Node> order() {
        return this.order;
    }

    /**
     * The the rank of node {@code n} in the computed order
     *
     * @param n the node
     * @return the position of node {@code n} in the computed order of the directed graph
     * @throws IllegalArgumentException if n doesn't belong to the graph
     */
    public int rank(final Node n) {
        validateNode(n);
        return this.rank.get(n);
    }

    // throw an IllegalArgumentException if n doesn't belong to the graph
    private void validateNode(final Node n) {
        if (!this.rank.containsKey(n)) {
            throw new IllegalArgumentException("node " + n + " doesn't belong to the graph");
        }
    }
}
