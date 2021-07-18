package de.obqo.decycle.model;

import static de.obqo.decycle.model.SliceType.classType;
import static de.obqo.decycle.model.SliceType.customType;
import static de.obqo.decycle.model.SliceType.packageType;

import java.util.Comparator;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * The class {@link Node} represents the nodes in a dependency and slice graph.
 * <p>
 * During the analysis Decycle will create {@link Node} instances by calling {@link #classNode(String)} that have the
 * {@link #type} {@link SliceType#classType()} for all classes and types it will encounter. After that Decycle will
 * create {@link Node} instances for groups of class nodes. The first grouping level creates nodes with {@link #type}
 * {@link SliceType#packageType()} (by calling {@link #packageNode(String)}) representing the packages of the analyzed
 * classes. Then, if rules for slicings have been defined, custom slice nodes will be created using {@link
 * #sliceNode(String, String)}, which use the name of the slicing as {@link SliceType#customType(String) custom type}.
 * <p>
 * The {@link #name} of a node is the class name, the package name, or the slice name.
 * <p>
 * Nodes are connected to other nodes with {@link Edge edges}.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Node implements Comparable<Node> {

    public static Node classNode(final String name) {
        return new Node(classType(), name);
    }

    public static Node packageNode(final String name) {
        return new Node(packageType(), name);
    }

    public static Node sliceNode(final String type, final String name) {
        return sliceNode(customType(type), name);
    }

    public static Node sliceNode(final SliceType type, final String name) {
        return new Node(type, name);
    }

    public static final Comparator<Node> COMPARATOR = Comparator.comparing(Node::getName);

    @NonNull SliceType type;

    @NonNull String name;

    public boolean hasType(final SliceType type) {
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
