package de.obqo.decycle.demo.helper;

public class StringHelper {

    public static String toRepeatedString(final int number) {
        return String.format("%d", number).repeat(number);
    }
}
