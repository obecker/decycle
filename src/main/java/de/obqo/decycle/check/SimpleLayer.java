package de.obqo.decycle.check;

import java.util.List;

public abstract class SimpleLayer implements Layer {

    private List<String> eSet;

    SimpleLayer(final String... es) {
        this.eSet = List.of(es);
    }

    List<String> getSlices() {
        return this.eSet;
    }

    @Override
    public boolean contains(final String elem) {
        return this.eSet.contains(elem);
    }
}
