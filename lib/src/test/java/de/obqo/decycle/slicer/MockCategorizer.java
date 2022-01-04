package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MockCategorizer implements Categorizer {

    private final Map<Node, Set<Node>> categoryMapping = new HashMap<>();

    public static MockCategorizer of(final Node node, final Node... categories) {
        return new MockCategorizer().with(node, categories);
    }

    public MockCategorizer with(final Node node, final Node... categories) {
        this.categoryMapping.put(node, Set.of(categories));
        return this;
    }

    @Override
    public Set<Node> apply(final Node node) {
        return this.categoryMapping.getOrDefault(node, NONE);
    }
}
