package de.obqo.decycle.slicer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternMatcher {

    private final Pattern pattern;

    public PatternMatcher(final String pattern) {
        this.pattern = Pattern.compile(ensureParens(escapeStars(escapeDots(pattern))));
    }

    public Optional<String> matches(final String name) {
        final Matcher matcher = this.pattern.matcher(name);
        return matcher.find() ? Optional.of(name.substring(matcher.start(1), matcher.end(1))) : Optional.empty();
    }

    private static String ensureParens(final String p) {
        return Pattern.matches(".*\\(.*\\).*", p) ? p : "(" + p + ")";
    }

    private static String escapeStars(final String p) {
        if (p.contains("***")) {
            throw new IllegalArgumentException("More than two '*'s in a row is not a supported pattern.");
        }
        final var doubleStarPlaceHolder = getPlaceHolder(p);
        final var singleStarPlaceHolder = getPlaceHolder(p + doubleStarPlaceHolder);
        return p.replace("**", doubleStarPlaceHolder)
                .replace("*", singleStarPlaceHolder)
                .replace(doubleStarPlaceHolder, ".*")
                .replace(singleStarPlaceHolder, "[^.]*");
    }

    private static String getPlaceHolder(final String pattern) {
        char c = 1;
        while (pattern.indexOf(c) >= 0) {
            c++;
        }
        return String.valueOf(c);
    }

    private static String escapeDots(final String p) {
        return p.replace(".", "\\.");
    }
}
