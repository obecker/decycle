package de.obqo.decycle.slicer;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * Matcher for ant-like patterns of dot-separated package and class names. Supports <code>?</code> for a single non-dot
 * character, <code>*</code> for zero or more non-dot characters, and <code>**</code> for any characters (including
 * dots). Moreover, a pipe symbol <code>|</code> can be used to express alternatives (possibly enclosed in
 * parentheses).
 * <p>
 * Examples: <code>some.package.module.Module?</code>, <code>some.package.**</code>, <code>some.package.*.**</code>,
 * <code>some.package.(foo|bar).**</code>
 * <p>
 * In <em>grouping</em> mode a pattern may contain at most one pair of curly braces. The method {@link #matches(String)}
 * will match the entire input string and returns an {@link Optional} containing the matching substring in curly braces,
 * or the entire string if there were no curly braces, or an empty <code>Optional</code> if the input doesn't match.
 * <p>
 * Example: <code>some.package.{*}.**</code> will match <code>some.package.foo.Class</code> and the match result is
 * <code>Optional.of("foo")</code>
 */
class PatternMatcher {

    // some placeholders for turning a decycle pattern into a regular expression
    private static final String SINGLE_STAR_PLACEHOLDER = "+";
    private static final String DOUBLE_STAR_PLACEHOLDER = "-";

    private static final String ALLOWED_SYMBOLS = ".?*|(){}";
    private static final Set<Integer> ALLOWED_SYMBOL_CHARS =
            ALLOWED_SYMBOLS.chars().boxed().collect(toUnmodifiableSet());

    private static final Map<Character, Character> PAREN_PAIRS = Map.of('}', '{', ')', '(');

    private static void validatePattern(final String p, final boolean grouping) {
        Preconditions.checkArgument(p != null, "Pattern string must not be null");
        validatePatternCharacters(p);
        validatePatternParentheses(p, grouping);
    }

    private static void validatePatternCharacters(final String p) {
        p.chars().forEach(c -> validatePatternCharacter(c, p));

        if (p.contains("***")) {
            throw new IllegalArgumentException(
                    format("More than two '*'s in a row is not a supported pattern - encountered '%s'", p));
        }
    }

    private static void validatePatternCharacter(final int c, final String p) {
        if (!isJavaIdentifierPart(c) && !ALLOWED_SYMBOL_CHARS.contains(c)) {
            throw new IllegalArgumentException(format(
                    "Pattern string may contain only characters, digits, and '%s' - encountered '%c' in pattern '%s'",
                    ALLOWED_SYMBOLS, c, p));
        }
    }

    private static void validatePatternParentheses(final String p, final boolean grouping) {
        int groupCount = 0;
        final Deque<Character> parens = new ArrayDeque<>();
        for (final char c : p.toCharArray()) {
            switch (c) {
            case '{':
                groupCount++;
                parens.push(c);
                break;
            case '(':
                parens.push(c);
                break;
            case '}':
            case ')':
                if (!PAREN_PAIRS.get(c).equals(parens.poll())) {
                    throw new IllegalArgumentException(
                            format("Unmatched right parenthesis '%c' found in pattern '%s'", c, p));
                }
                break;
            }
        }
        if (!parens.isEmpty()) {
            throw new IllegalArgumentException(
                    format("Unmatched left parenthesis '%c' found in pattern '%s'", parens.poll(), p));
        }
        if (grouping && groupCount > 1) {
            throw new IllegalArgumentException(
                    format("More than one pair of curly braces are not allowed in the pattern '%s'", p));
        }
        if (!grouping && groupCount > 0) {
            throw new IllegalArgumentException(
                    format("Curly braces are only allowed in slicing patterns. Encountered '%s'", p));
        }
    }

    private final Pattern pattern;
    private final int group;

    public PatternMatcher(final String p) {
        this(p, false);
    }

    public PatternMatcher(final String p, final boolean grouping) {
        validatePattern(p, grouping);

        this.group = determineGroup(p);
        this.pattern = Pattern.compile(replaceGroupingParens(replaceWildcards(escapeLiterals(p))));
    }

    public Optional<String> matches(final String name) {
        final Matcher matcher = this.pattern.matcher(name);
        return matcher.matches() ? Optional.of(matcher.group(this.group)) : Optional.empty();
    }

    private static String replaceWildcards(final String p) {
        return p.replace("**", DOUBLE_STAR_PLACEHOLDER)
                .replace("*", SINGLE_STAR_PLACEHOLDER)
                .replace(DOUBLE_STAR_PLACEHOLDER, ".*")
                .replace(SINGLE_STAR_PLACEHOLDER, "[^.]*")
                .replace("?", "[^.]");
    }

    private static String escapeLiterals(final String p) {
        return p.replace(".", "\\.").replace("$", "\\$");
    }

    private static String replaceGroupingParens(final String p) {
        return p.replace('{', '(').replace('}', ')');
    }

    private static int determineGroup(final String p) {
        int group = 0;
        for (int i = 0; i < p.length(); i++) {
            switch (p.charAt(i)) {
            case '(':
                group++;
                break;
            case '{':
                group++;
                return group;
            }
        }
        return 0;
    }
}
