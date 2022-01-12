package de.obqo.decycle.report;

import org.javastack.fontmetrics.SimpleFontMetrics;

import lombok.RequiredArgsConstructor;

/**
 * Helper class to work around a problem with different system font metrics in different environments.
 */
@RequiredArgsConstructor
class FontMetricsSupport {

    private final SimpleFontMetrics.FontMetricsHelper delegate;

    static FontMetricsSupport get(final boolean minify) {
        // minify=false is only used for testing - in that case we force using the IndexedFontMetrics to get
        // deterministic results on all platforms (namely local and in github actions)
        SimpleFontMetrics.FontMetricsHelper delegate = SimpleFontMetrics.SystemFontMetrics.getDefaultInstance();
        if (delegate == null || !minify) {
            delegate = SimpleFontMetrics.IndexedFontMetrics.getDefaultInstance();
        }

        return new FontMetricsSupport(delegate);
    }

    int widthOf(final String text) {
        return this.delegate.widthOf(text);
    }
}
