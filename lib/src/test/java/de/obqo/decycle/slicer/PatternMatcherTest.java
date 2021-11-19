package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class PatternMatcherTest {

    @Test
    void shouldNotMatchArbitraryObject() {
        final var matcher = new PatternMatcher("(some.package.Class)");

        assertThat(matcher.matches("x")).isEmpty();
    }

    @Test
    void shouldMatchFixPattern() {
        final var matcher = new PatternMatcher("some.package.Class");

        assertThat(matcher.matches("some.package.Class")).hasValue("some.package.Class");
    }

    @Test
    void shouldNotMatchOnlySubPattern() {
        final var matcher = new PatternMatcher("some.package.Class");

        assertThat(matcher.matches("tld.some.package.Class$Inner")).isEmpty();
    }

    @Test
    void shouldReturnOnlyMatchedGroup() {
        final var matcher = new PatternMatcher("some.{package}.Class", true);

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
    }

    @Test
    void starShouldMatchArbitraryLettersWithoutGrouping() {
        final var matcher = new PatternMatcher("some.*.Class", false);

        assertThat(matcher.matches("some.package.Class")).hasValue("some.package.Class");
        assertThat(matcher.matches("some.mph.Class")).hasValue("some.mph.Class");
    }

    @Test
    void starShouldNotMatchDots() {
        final var matcher = new PatternMatcher("some.*.Class");

        assertThat(matcher.matches("some.pack.age.Class")).isEmpty();
    }

    @Test
    void dotsShouldNotMatchLetters() {
        final var matcher = new PatternMatcher("some.pack.age.Class");

        assertThat(matcher.matches("some.packxage.Class")).isEmpty();
    }

    @Test
    void starShouldMatchArbitraryLettersWithGrouping() {
        final var matcher = new PatternMatcher("some.{*}.Class", true);

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
        assertThat(matcher.matches("some.mph.Class")).hasValue("mph");
    }

    @Test
    void shouldMatchCorrectGroupWithParentheses() {
        final var matcher = new PatternMatcher("(some.(foo|bar).{*})", true);

        assertThat(matcher.matches("some.foo.Class")).hasValue("Class");
    }

    @Test
    void doubleStarShouldMatchSinglePackageLevel() {
        final var matcher = new PatternMatcher("some.{**}.Class", true);

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
    }

    @Test
    void doubleStarShouldMatchMultiplePackageLevels() {
        final var matcher = new PatternMatcher("some.{**}.Class", true);

        assertThat(matcher.matches("some.pack.age.Class")).hasValue("pack.age");
    }

    @ParameterizedTest
    @ValueSource(strings = { "de.(one|two).(*).**", "de.(one.(*)).**" })
    void multipleParensAreFine(final String pattern) {
        assertThatCode(() -> new PatternMatcher(pattern)).doesNotThrowAnyException();
    }

    @Test
    void dollarSymbolMatchesAsLiteral() {
        final var matcher = new PatternMatcher("base.package.Class$*");

        assertThat(matcher.matches("base.package.Class$Inner")).hasValue("base.package.Class$Inner");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("illegalPatterns")
    void shouldThrowExceptionForIllegalPatterns(final String pattern, final String expectedMessage) {
        assertThatIllegalArgumentException().isThrownBy(() -> new PatternMatcher(pattern, false))
                .withMessage(expectedMessage);
        assertThatIllegalArgumentException().isThrownBy(() -> new PatternMatcher(pattern, true))
                .withMessage(expectedMessage);
    }

    static Stream<Arguments> illegalPatterns() {
        return Stream.of(
                arguments(null, "Pattern string must not be null"),
                arguments("base.[a-z]+.class",
                        "Pattern string may contain only characters, digits, and '.*|(){}' - " +
                                "encountered '[' in pattern 'base.[a-z]+.class'"),
                arguments("abc(", "Unmatched left parenthesis '(' found in pattern 'abc('"),
                arguments("abc{", "Unmatched left parenthesis '{' found in pattern 'abc{'"),
                arguments("abc)", "Unmatched right parenthesis ')' found in pattern 'abc)'"),
                arguments("abc}", "Unmatched right parenthesis '}' found in pattern 'abc}'"),
                arguments("abc(})", "Unmatched right parenthesis '}' found in pattern 'abc(})'"),
                arguments("abc{)}", "Unmatched right parenthesis ')' found in pattern 'abc{)}'"),
                arguments("a.***.b", "More than two '*'s in a row is not a supported pattern - encountered 'a.***.b'")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = { "de.{one|two}.{*}.**", "de.{one.{*}}.**" })
    void shouldThrowExceptionForMultipleCurlyBracesInGroupingMode(final String pattern) {
        assertThatIllegalArgumentException().isThrownBy(() -> new PatternMatcher(pattern, true))
                .withMessage("More than one pair of curly braces are not allowed in the pattern '" + pattern + "'");
    }

    @Test
    void shouldThrowExceptionForCurlyBracesInDefaultMode() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PatternMatcher("de.example.{*}"))
                .withMessage("Curly braces are only allowed in slicing patterns. Encountered 'de.example.{*}'");
    }
}
