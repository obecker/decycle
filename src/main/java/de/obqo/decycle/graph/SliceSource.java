package de.obqo.decycle.graph;

import java.util.Set;

public interface SliceSource {

    Set<String> sliceTypes();

    Slice slice(String name);

}
