package de.obqo.decycle.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PatternTest {

    @ParameterizedTest
    @ValueSource(strings = { "com.example.Test", "com.example.*", "" })
    void shouldParseUnnamedPatterns(final String pattern) {
        assertThat(Pattern.parse(pattern)).isInstanceOf(UnnamedPattern.class).asString().isEqualTo(pattern);
    }

    @ParameterizedTest
    @ValueSource(strings = { "com.example.Test=", "com.example.*=abc", "com.example.**=x=y", "=foo", "=" })
    void shouldParseNamedPatterns(final String pattern) {
        assertThat(Pattern.parse(pattern)).isInstanceOf(NamedPattern.class).asString().isEqualTo(pattern);
    }
}
