package de.obqo.decycle.check;

public class LenientLayer extends SimpleLayer {

    public LenientLayer(final String... es) {
        super(es);
    }

    @Override
    public boolean denyDependenciesWithinLayer() {
        return false;
    }
}
