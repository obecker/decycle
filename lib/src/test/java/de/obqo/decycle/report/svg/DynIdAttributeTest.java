package de.obqo.decycle.report.svg;

import static de.obqo.decycle.report.svg.DynIdAttribute.dynId;
import static de.obqo.decycle.report.svg.DynIdAttribute.dynRef;
import static de.obqo.decycle.report.svg.DynIdAttribute.dynUrl;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import j2html.attributes.Attribute;

class DynIdAttributeTest {

    @Test
    void shouldCreateDynamicAttributes() {
        // when
        Attribute attr = dynId("test");

        // then
        assertThat(attr.getName()).isEqualTo("id");
        assertThat(attr.getValue()).isEqualTo("test1");
        assertThat(dynRef("test")).isEqualTo("#test1");
        assertThat(dynUrl("test")).isEqualTo("url(#test1)");

        // when
        attr = dynId("test");

        // then
        assertThat(attr.getValue()).isEqualTo("test2");
        assertThat(dynRef("test")).isEqualTo("#test2");
        assertThat(dynUrl("test")).isEqualTo("url(#test2)");
    }
}
