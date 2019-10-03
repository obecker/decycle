package de.obqo.decycle.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {

    public static <T> T defaultValue(final T val, final T defaultVal) {
        return val == null ? defaultVal : val;
    }
}
