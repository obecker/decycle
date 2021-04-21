package de.obqo.decycle.check;

import java.util.List;

abstract class SimpleLayer implements Layer {

    private final boolean denyDependenciesWithinLayer;
    private final List<String> slices;

    SimpleLayer(final boolean denyDependenciesWithinLayer, final String... slices) {
        this.denyDependenciesWithinLayer = denyDependenciesWithinLayer;
        this.slices = List.of(slices);
    }

    @Override
    public List<String> getSlices() {
        return this.slices;
    }

    @Override
    public boolean contains(final String slice) {
        return this.slices.contains(slice);
    }

    @Override
    public boolean denyDependenciesWithinLayer() {
        return this.denyDependenciesWithinLayer;
    }
}
