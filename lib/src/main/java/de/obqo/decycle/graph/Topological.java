package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class for determining a topological order of a <em>directed acyclic graph</em> (DAG). A directed graph has a
 * topological order if and only if it is a DAG. If the graph is not acyclic the returned order is not a topological
 * order - this implementation doesn't check for cycles.
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

    /**
     * Returns a topological order if the directed graph is acyclic, and some other order otherwise.
     * <p>
     * This implementation uses depth-first search. This method takes &Theta;(<em>V</em> + <em>E</em>) time in the worst
     * case, where <em>V</em> is the number of nodes (or vertices) and <em>E</em> is the number of edges. It uses
     * &Theta;(<em>V</em>) extra space (not including the directed graph).
     * <p>
     * (Note: the last paragraph concerning its time behavior is no longer true, since we are sorting the nodes and
     * edges to guarantee a deterministic order. This sorting might be removed if the incoming graph guarantees already
     * a defined order when traversing its nodes and edges.)
     *
     * @return the nodes in topological order if the directed graph has a topological order (or equivalently, if the
     * digraph is a DAG), and some other order otherwise
     */
    static List<Node> order(final Slicing slicing) {
        return new DepthFirstOrder(slicing).reversePost();
    }

    /**
     * The {@code DepthFirstOrder} class represents a data type for determining depth-first search ordering of the nodes
     * in a directed graph. This specific implementation computes only the reverse postorder.
     * <p>
     * This implementation uses depth-first search. The constructor takes &Theta;(<em>V</em> + <em>E</em>) time, where
     * <em>V</em> is the number of nodes (or vertices) and <em>E</em> is the number of edges. Each instance method
     * takes &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not including the directed graph).
     * <p>
     * (Note: the last paragraph concerning its time behavior is no longer true, since we are sorting the nodes and
     * edges to guarantee a deterministic order. This sorting might be removed if the incoming graph guarantees already
     * a defined order when traversing its nodes and edges.)
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
    private static class DepthFirstOrder {

        // contains all nodes that have been marked in depth first search
        private final Set<Node> marked = new HashSet<>();
        // nodes in postorder
        private final List<Node> postorder = new LinkedList<>();

        /**
         * Determines a depth-first order for the directed graph {@code g}.
         *
         * @param g the directed graph
         */
        DepthFirstOrder(final Slicing g) {
            for (final Node node : sorted(g.nodes(), Node.COMPARATOR.reversed())) { // for a deterministic order
                if (!this.marked.contains(node)) {
                    depthFirstSearch(g, node);
                }
            }
        }

        // run DFS in the directed graph g from node node and compute postorder
        private void depthFirstSearch(final Slicing g, final Node node) {
            this.marked.add(node);
            for (final Edge e : sorted(g.outEdges(node), Edge.COMPARATOR.reversed())) { // for a deterministic order
                final Node to = e.getTo();
                if (e.isIncluded() && !this.marked.contains(to)) {
                    depthFirstSearch(g, to);
                }
            }
            this.postorder.add(node);
        }

        private <T> Set<T> sorted(final Set<T> set, final Comparator<T> comparator) {
            final Set<T> result = new TreeSet<>(comparator);
            result.addAll(set);
            return result;
        }

        /**
         * Returns the nodes in reverse postorder.
         *
         * @return the nodes in reverse postorder
         */
        List<Node> reversePost() {
            final List<Node> reverse = new ArrayList<>(this.postorder);
            Collections.reverse(reverse);
            return reverse;
        }
    }
}
