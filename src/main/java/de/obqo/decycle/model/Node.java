package de.obqo.decycle.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Node {

    public static final String CLASS = "Class";
    public static final String PACKAGE = "Package";

    private final @NonNull String type;

    private final @NonNull String name;

    public boolean hasType(final String type) {
        return type.equals(this.type);
    }

    public static Node classNode(final String name) {
        return new Node(CLASS, name);
    }

    public static Node packageNode(final String name) {
        return new Node(PACKAGE, name);
    }

    public static Node sliceNode(final String type, final String name) {
        return new Node(type, name);
    }
}
