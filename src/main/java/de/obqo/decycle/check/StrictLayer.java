package de.obqo.decycle.check;

/**
 * In a {@link StrictLayer} the contained slices must not depend on each other.
 */
final class StrictLayer extends SimpleLayer {

    StrictLayer(final String... slices) {
        super(true, slices);
    }
}
