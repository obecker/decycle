package de.obqo.decycle.model;

import java.util.Set;

public interface Node {

    boolean contains(Node n);

    Set<String> getTypes();

    String getName();
}
