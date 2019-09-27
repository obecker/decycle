package de.obqo.decycle.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class SimpleNode implements Node {

    private enum NodeType {
        CLASS,
        PACKAGE
    }

    private final NodeType nodeType;
    private final String name;

    public static SimpleNode classNode(String name) {
        return new SimpleNode(NodeType.CLASS, name);
    }

    public static SimpleNode packageNode(String name) {
        return new SimpleNode(NodeType.PACKAGE, name);
    }

    public SimpleNode toPackageNode() {
        if (this.nodeType == NodeType.PACKAGE) {
            return this;
        }
        final int classNameIndex = this.name.lastIndexOf('.');
        final String packageName = classNameIndex < 0 ? "" : this.name.substring(0, classNameIndex);
        return packageNode(packageName);
    }
}
