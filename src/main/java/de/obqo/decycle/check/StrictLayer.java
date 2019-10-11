package de.obqo.decycle.check;

public class StrictLayer extends SimpleLayer {

    public StrictLayer(final String... es) {
        super(es);
    }

    @Override
    public boolean denyDependenciesWithinLayer() {
        return true;
    }
}
