package de.obqo.decycle.analysis;

import static de.obqo.decycle.model.SliceType.classType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import de.obqo.decycle.model.Node;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GraphBuilderTest {

    @ParameterizedTest
    @MethodSource
    void shouldCreateClassNodeFromTypeName(final String slashSeparatedName, final String expectedNodeName) {
        // when
        final Node node = GraphBuilder.classNodeFromTypeName(slashSeparatedName);

        // then
        assertThat(node.hasType(classType())).isTrue();
        assertThat(node.getName()).isEqualTo(expectedNodeName);
    }

    static Stream<Arguments> shouldCreateClassNodeFromTypeName() {
        return Stream.of(
                arguments("de/obqo/decycle/Test", "de.obqo.decycle.Test"),
                arguments("de/obqo/decycle/Test$1", "de.obqo.decycle.Test"),
                arguments("de/obqo/decycle/Test$1$23", "de.obqo.decycle.Test"),
                arguments("de/obqo/decycle/Test$56$Inner", "de.obqo.decycle.Test$Inner"),
                arguments("de/obqo/decycle/Test$2Local", "de.obqo.decycle.Test$2Local"),
                arguments("de/obqo/decycle/Test$23Local", "de.obqo.decycle.Test$23Local"),
                arguments("de/obqo/decycle/Test$1$23Local$4", "de.obqo.decycle.Test$23Local")
        );
    }
}
