package de.obqo.decycle.slicer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

class PatternMatcher {

    private final Pattern pattern;

    public PatternMatcher(final String pattern) {
        this(pattern, false);
    }

    public PatternMatcher(final String pattern, final boolean strict) {
        Preconditions.checkNotNull(pattern, "pattern string must not be null");
        this.pattern = Pattern.compile(ensureParens(escapeStars(escapeDots(pattern)), strict));
    }

    public Optional<String> matches(final String name) {
        final Matcher matcher = this.pattern.matcher(name);
        return matcher.matches() ? Optional.of(name.substring(matcher.start(1), matcher.end(1))) : Optional.empty();
    }

    private static String ensureParens(final String p, final boolean strict) {
        if (strict && Pattern.matches(".*\\(.*\\(.*", p)) {
            throw new IllegalArgumentException("More than one pair of parentheses is not a supported pattern.");
        }

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
