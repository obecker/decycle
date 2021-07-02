package de.obqo.decycle.report;

import org.javastack.fontmetrics.SimpleFontMetrics;

import lombok.RequiredArgsConstructor;

/**
 * Helper class to work around a problem with different system font metrics in different environments. Might be removed
 * if https://github.com/ggrandes/fontmetrics/issues/1 gets resolved.
 */
@RequiredArgsConstructor
class FontMetricsSupport {

    private static SimpleFontMetrics.FontMetricsHelper defaultDelegate;
    private static SimpleFontMetrics.FontMetricsHelper indexedDelegate;

    private final SimpleFontMetrics.FontMetricsHelper delegate;

    static {
        try {
            indexedDelegate = SimpleFontMetrics.IndexedFontMetrics
                    .importFile(SimpleFontMetrics.class.getResource("/fontmetrics.bin"));
        } catch (final Exception e) {
            throw new ExceptionInInitializerError(e);
        }

        try {
            defaultDelegate = new SimpleFontMetrics.SystemFontMetrics();
        } catch (final Throwable e) {
            defaultDelegate = indexedDelegate;
        }
    }

    static FontMetricsSupport get(final boolean minify) {
        // minify=false is only used for testing - in that case we force using the IndexedFontMetrics to get
        // deterministic results on all platforms (namely local and in github actions)
        return minify ? new FontMetricsSupport(defaultDelegate) : new FontMetricsSupport(indexedDelegate);
    }

    int widthOf(final String text) {
        return this.delegate.widthOf(text);
    }
}
