package de.obqo.decycle.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class ConfigurationTest {

    @Test
    void projectConfigurationShouldHaveNoConstraintViolations() {
        assertThat(Configuration.builder().classpath("build").includes(List.of("de.obqo.decycle.**")).build().check())
                .isEmpty();
    }
}
