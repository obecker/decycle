package de.obqo.decycle.check;

public interface Layer {

    boolean contains(String elem);

    boolean denyDependenciesWithinLayer();

}
