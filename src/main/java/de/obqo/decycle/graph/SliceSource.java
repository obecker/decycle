package de.obqo.decycle.graph;

import java.util.Set;

import com.google.common.graph.Network;
import de.obqo.decycle.model.Node;

public interface SliceSource {

    Set<String> slices();

    Network<Node, Edge> slice(String name);

}
