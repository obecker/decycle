package de.obqo.decycle.report.svg;

import java.text.NumberFormat;
import java.util.Locale;

class NumberFormatter {

    private static NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumIntegerDigits(0);
        numberFormat.setGroupingUsed(false);
    }

    public static String formatNumber(final double d) {
        return numberFormat.format(d);
    }
}
