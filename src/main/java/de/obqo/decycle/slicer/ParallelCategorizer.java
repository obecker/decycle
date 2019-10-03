package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.util.Assert;

import java.util.List;

public class ParallelCategorizer implements Categorizer {

    private final List<Categorizer> cs;

    public ParallelCategorizer(final Categorizer... cs) {
        Assert.notNull(cs, "Missing categorizers for ParallelCategorizer");
        this.cs = List.of(cs);
    }

    @Override
    public Node apply(final Node node) {
        if (node instanceof ParentAwareNode) {
            return ((ParentAwareNode) node).next();
        }
        return this.cs.isEmpty() ? node : new ParentAwareNode(
                this.cs.stream().map(c -> c.apply(node)).toArray(Node[]::new)
        ).prune();
    }
}
