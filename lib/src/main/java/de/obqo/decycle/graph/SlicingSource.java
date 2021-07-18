package de.obqo.decycle.graph;

import de.obqo.decycle.model.SliceType;

import java.util.Set;

public interface SlicingSource {

    Set<SliceType> sliceTypes();

    Slicing slicing(SliceType sliceType);

}
