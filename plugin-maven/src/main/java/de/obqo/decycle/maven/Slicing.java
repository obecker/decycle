package de.obqo.decycle.maven;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for slicings (within &lt;slicings&gt;&lt;/slicings&gt;):
 */
@Getter
@Setter
public class Slicing {

    private String name;
    private String patterns;

}
