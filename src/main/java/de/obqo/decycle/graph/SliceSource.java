package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import java.util.Set;

import com.google.common.graph.Network;

public interface SliceSource {

    Set<String> slices();

    Network<Node, Edge> slice(String name);

}
