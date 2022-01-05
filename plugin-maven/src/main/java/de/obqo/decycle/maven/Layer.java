package de.obqo.decycle.maven;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Layer {

    private boolean strict;
    private String slices;

}
