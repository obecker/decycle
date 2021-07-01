package de.obqo.decycle.model;

import java.util.Comparator;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * The class {@link Node} represents the nodes in a dependency and slice graph.
 * <p>
 * During the analysis Decycle will create {@link Node} instances by calling {@link #classNode(String)} that have the
 * {@link #type} {@link #CLASS} for all classes and types it will encounter. After that Decycle will create {@link Node}
 * instances for groups of class nodes. The first grouping level creates nodes with {@link #type} {@link #PACKAGE} (by
 * calling {@link #packageNode(String)}) representing the packages of the analyzed classes. Then, if rules for slicings
 * have been defined, slice nodes will be created using {@link #sliceNode(String, String)}, which have the name of the
 * slicing as {@link #type}.
 * <p>
 * The {@link #name} of a node is the class name, the package name, or the slice name.
 * <p>
 * Nodes are connected to other nodes with {@link Edge edges}.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Node implements Comparable<Node> {

    public static final String CLASS = "Class";
    public static final String PACKAGE = "Package";

    public static Node classNode(final String name) {
        return new Node(CLASS, name);
    }

    public static Node packageNode(final String name) {
        return new Node(PACKAGE, name);
    }

    public static Node sliceNode(final String type, final String name) {
        return new Node(type, name);
    }

    public static final Comparator<Node> COMPARATOR = Comparator.comparing(Node::getName);

    private final @NonNull String type;

    private final @NonNull String name;

    public boolean hasType(final String type) {
        return type.equals(this.type);
    }

    public String displayString() {
        return this.name;
    }

    @Override
    public int compareTo(final Node other) {
        return COMPARATOR.compare(this, other);
    }
}
