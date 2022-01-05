package de.obqo.decycle.maven;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

/**
 * Base class for {@code &lt;allow&gt;} and {@code &lt;allow-direct&gt;} configuration elements. May either be used with
 * a simple (string) content
 * <pre>
 * &lt;allow&gt;slice1, slice2, ...&lt;/allow&gt;
 * </pre>
 * or with {@code &lt;any-of&gt;} / {@code &lt;one-of&gt;} children.
 * <pre>
 * &lt;allow&gt;
 *    &lt;one-of&gt;slice1, slice2&lt;one-of&gt;
 *    &lt;any-of&gt;slice3&lt;any-of&gt;
 *    &lt;any-of&gt;slice4, slice5&lt;any-of&gt;
 * &lt;/allow&gt;
 * </pre>
 * Mixing these two configuration types (i.e. using <em>XML mixed content</em>) is not possible.
 */
@Getter
@ToString
public abstract class AllowConstraint {

    private final boolean direct;
    private final List<Layer> layers = new ArrayList<>();

    /**
     * simple content
     */
    @Getter(AccessLevel.NONE)
    private String content;

    protected AllowConstraint(final boolean direct) {
        this.direct = direct;
    }

    /**
     * Adds an {@code &lt;any-of&gt;slice1, slice2, ...&lt;/any-of&gt;} layer
     *
     * @param slices comma separated list of slice names
     */
    public void setAnyOf(final String slices) {
        this.layers.add(new Layer(false, slices));
    }

    /**
     * Adds a {@code &lt;one-of&gt;slice1, slice2, ...&lt;/one-of&gt;} layer
     *
     * @param slices comma separated list of slice names
     */
    public void setOneOf(final String slices) {
        this.layers.add(new Layer(true, slices));
    }

    /**
     * Default setter for string-only content, for example {@code &lt;allow&gt;slice1, slice2, ...&lt;/allow&gt;}
     * <p>
     * Will be interpreted as list of {@code &lt;any-of&gt;} elements.
     *
     * @param slices comma separated list of slice names
     */
    public void set(final String slices) {
        this.content = slices;
    }

    public String get() {
        return this.content;
    }
}
