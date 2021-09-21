package de.obqo.decycle.report;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IdMapperTest {

    @Test
    void shouldGenerateIds() {
        // given
        final IdMapper<Object> idMapper = new IdMapper<>("o");
        final Object o1 = new Object();
        final Object o2 = new Object();

        // then
        assertThat(idMapper.getId(o1)).isEqualTo("o1");
        assertThat(idMapper.getId(o2)).isEqualTo("o2");
        assertThat(idMapper.getId(o1)).isEqualTo("o1");
        assertThat(idMapper.getId(o2)).isEqualTo("o2");
    }
}
