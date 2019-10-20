package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import lombok.Value;

@Value
public class Edge {

    public enum EdgeLabel {
        CONTAINS, REFERENCES
    }

    private final Node from;
    private final Node to;
    private final EdgeLabel label;
}
