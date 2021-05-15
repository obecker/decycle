package de.obqo.decycle.check;

/**
 * In a {@link LenientLayer} the contained slices may depend on each other.
 */
final class LenientLayer extends SimpleLayer {

    LenientLayer(final String... slices) {
        super(false, slices);
    }
}
