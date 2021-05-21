package de.obqo.decycle.graph;

import java.util.Set;

public interface SlicingSource {

    Set<String> sliceTypes();

    Slicing slicing(String name);

}
