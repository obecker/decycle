package de.obqo.decycle.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Assert {

    public static void notNull(final Object o, final String message) {
        if (o == null) {
            throw new IllegalStateException(message);
        }
    }

    public static void isTrue(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
