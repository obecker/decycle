package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class PatternMatcherTest {

    @Test
    void shouldNotMatchArbitraryObject() {
        final var matcher = new PatternMatcher("(some.package.Class)");

        assertThat(matcher.matches("x")).isEmpty();
    }

    @Test
    void shouldMatchFixPattern() {
        final var matcher = new PatternMatcher("(some.package.Class)");

        assertThat(matcher.matches("some.package.Class")).hasValue("some.package.Class");
    }

    @Test
    void shouldMatchEntirePattern() {
        final var matcher = new PatternMatcher("(some.package.Class)");

        assertThat(matcher.matches("tld.some.package.Class$Inner")).isEmpty();
    }

    @Test
    void shouldMatchEntirePatternIfNoParensArePresent() {
        final var matcher = new PatternMatcher("some.package.Class");

        assertThat(matcher.matches("some.package.Class")).hasValue("some.package.Class");
    }

    @Test
    void shouldReturnOnlyMatchedGroup() {
        final var matcher = new PatternMatcher("some.(package).Class");

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
    }

    @Test
    void starShouldMatchArbitraryLetters() {
        final var matcher = new PatternMatcher("some.(*).Class");

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
        assertThat(matcher.matches("some.mph.Class")).hasValue("mph");
    }

    @Test
    void starShouldNotMatchDots() {
        final var matcher = new PatternMatcher("some.(*).Class");

        assertThat(matcher.matches("some.pack.age.Class")).isEmpty();
    }

    @Test
    void dotsShouldNotMatchLetters() {
        final var matcher = new PatternMatcher("some.pack.age.Class");

        assertThat(matcher.matches("some.packxage.Class")).isEmpty();
    }

    @Test
    void starStarShouldMatchSinglePackageLevel() {
        final var matcher = new PatternMatcher("some.(**).Class");

        assertThat(matcher.matches("some.package.Class")).hasValue("package");
    }

    @Test
    void starStarShouldMatchMultiplePackageLevels() {
        final var matcher = new PatternMatcher("some.(**).Class");

        assertThat(matcher.matches("some.pack.age.Class")).hasValue("pack.age");
    }

    @Test
    void threeStarsShouldThrowAnException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PatternMatcher("invalid***pattern"))
                .withMessage("More than two '*'s in a row is not a supported pattern.");
    }

    @Test
    void twoPairsOfParensInStrictModeShouldThrowAnException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PatternMatcher("de.(one|two).(*).**", true))
                .withMessage("More than one pair of parentheses is not a supported pattern.");
    }

    @Test
    void nestedParensInStrictModeShouldThrowAnException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PatternMatcher("de.(one.(*)).**", true))
                .withMessage("More than one pair of parentheses is not a supported pattern.");
    }

    @Test
    void multipleParensInDefaultModeAreFine() {
        assertThatCode(() -> new PatternMatcher("de.(one.(*)).**"))
                .doesNotThrowAnyException();
    }

    @Test
    void nestedParensInDefaultModeAreFine() {
        assertThatCode(() -> new PatternMatcher("de.(one|two).(*).**"))
                .doesNotThrowAnyException();
    }
}
