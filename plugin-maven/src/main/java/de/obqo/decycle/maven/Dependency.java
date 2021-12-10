package de.obqo.decycle.maven;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for ignored dependencies (within &lt;ignoring&gt;&lt;/ignoring&gt;):
 */
@Getter
@Setter
public class Dependency {

    private String from;
    private String to;

}
