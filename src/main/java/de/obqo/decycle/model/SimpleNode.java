package de.obqo.decycle.model;

import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class SimpleNode implements Node {

    public static final String CLASS = "Class";
    public static final String PACKAGE = "Package";

    private final String nodeType;
    private final String name;

    public static SimpleNode classNode(final String name) {
        return new SimpleNode(CLASS, name);
    }

    public static SimpleNode packageNode(final String name) {
        return new SimpleNode(PACKAGE, name);
    }

    public static SimpleNode simpleNode(final String type, final String name) {
        return new SimpleNode(type, name);
    }

    @Override
    public boolean contains(final Node n) {
        return equals(n);
    }

    @Override
    public Set<String> getTypes() {
        return Set.of(this.nodeType);
    }
}
