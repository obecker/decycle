package de.obqo.decycle.check;

import java.util.List;

abstract class SimpleLayer implements Layer {

    private final boolean denyDependenciesWithinLayer;
    private final List<String> eSet;

    SimpleLayer(final boolean denyDependenciesWithinLayer, final String... es) {
        this.denyDependenciesWithinLayer = denyDependenciesWithinLayer;
        this.eSet = List.of(es);
    }

    @Override
    public List<String> getSlices() {
        return this.eSet;
    }

    @Override
    public boolean contains(final String elem) {
        return this.eSet.contains(elem);
    }

    @Override
    public boolean denyDependenciesWithinLayer() {
        return this.denyDependenciesWithinLayer;
    }
}
