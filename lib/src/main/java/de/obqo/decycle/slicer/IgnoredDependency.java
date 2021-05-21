package de.obqo.decycle.slicer;

import lombok.Value;

@Value
public class IgnoredDependency {

    String fromPattern;
    String toPattern;

    @Override
    public String toString() {
        return this.fromPattern + " → " + this.toPattern;
    }
}
