package de.obqo.decycle.analysis;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.NodeFilter;

import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncludeExcludeFilter implements NodeFilter {

    private final Set<NodeFilter> include;
    private final Set<NodeFilter> exclude;

    @Override
    public boolean test(final Node node) {
        return doesInclude(node) && this.exclude.stream().noneMatch(ex -> ex.test(node));
    }

    private boolean doesInclude(final Node node) {
        return this.include.isEmpty() || this.include.stream().anyMatch(in -> in.test(node));
    }
}
