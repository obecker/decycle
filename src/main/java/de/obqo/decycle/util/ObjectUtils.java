package de.obqo.decycle.util;

public class ObjectUtils {

    public static <T> T defaultValue(T val, T defaultVal) {
        return val == null ? defaultVal : val;
    }
}
