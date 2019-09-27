package de.obqo.decycle.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class ParentAwareNode implements Node {

    private final List<Node> vals;

    public ParentAwareNode(final Node... vals) {
        this.vals = List.of(vals);
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
}
