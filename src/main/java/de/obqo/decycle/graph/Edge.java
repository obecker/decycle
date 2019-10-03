package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Edge {

    public enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    private final Node from;
    private final Node to;
    private final EdgeLabel label;
}
