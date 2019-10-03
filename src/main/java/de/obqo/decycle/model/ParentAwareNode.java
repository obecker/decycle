package de.obqo.decycle.model;

import de.obqo.decycle.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ParentAwareNode implements Node {

    private final List<Node> vals;

    public ParentAwareNode(final Node... vals) {
        Assert.notNull(vals, "missing nodes for ParentAwareNode");
        this.vals = List.of(vals);
    }

    private ParentAwareNode(final List<Node> vals) {
        this.vals = vals;
    }

    @Override
    public boolean contains(final Node n) {
        return this.vals.stream().anyMatch(v -> v.contains(n));
    }

    @Override
    public Set<String> getTypes() {
        return this.vals.stream().flatMap(v -> v.getTypes().stream()).collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return this.vals.stream().map(Node::getName).collect(Collectors.joining(" x "));
    }

    public Node prune() {
        return this.vals.size() == 1 ? this.vals.get(0) : this;
    }

    public ParentAwareNode next() {
        return this.vals.size() > 1 ? new ParentAwareNode(this.vals.subList(1, this.vals.size())) : this;
    }

    public Node head() {
        return this.vals.get(0);
    }
}
