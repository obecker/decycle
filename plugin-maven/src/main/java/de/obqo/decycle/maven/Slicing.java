package de.obqo.decycle.maven;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration class for slicings (within &lt;slicings&gt;&lt;/slicings&gt;):
 */
@Getter
@Setter
@ToString
public class Slicing {

    private String name;
    private String patterns;
    private AllowConstraint[] constraints;

    public String getName() {
        if (this.name == null || this.name.isBlank()) {
            throw new IllegalArgumentException("Missing name of slicing with patterns " + this.patterns);
        }
        return this.name;
    }
}
