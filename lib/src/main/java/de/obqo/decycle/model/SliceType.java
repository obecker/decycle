package de.obqo.decycle.model;

import java.util.Comparator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Instances of {@link SliceType} define the {@link Node#getType() type} of a {@link Node}. There is a {@link
 * #classType()} for nodes representing a single Java class, there is a {@link #packageType()} for nodes representing a
 * package, and there is {@link #customType(String)} for nodes representing custom slices, e.g. groups of packages. Each
 * slice type has a name that represents the name of the slicing. For example, we might define a custom slicing
 * "Quarter" that maps each package to one of 4 slices. That will result in 4 nodes in which each is a custom slice and
 * all have the type {@code sliceType("Quarter")}.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SliceType implements Comparable<SliceType> {

    enum Kind {
        CLASS, PACKAGE, CUSTOM
    }

    private static final Comparator<SliceType> COMPARATOR =
            Comparator.comparing(SliceType::getKind).thenComparing(SliceType::getName);

    private static final SliceType CLASS_TYPE = new SliceType(Kind.CLASS, "Class");
    private static final SliceType PACKAGE_TYPE = new SliceType(Kind.PACKAGE, "Package");

    /**
     * @return the singleton class type
     */
    public static SliceType classType() {
        return CLASS_TYPE;
    }

    /**
     * @return the singleton package type
     */
    public static SliceType packageType() {
        return PACKAGE_TYPE;
    }

    /**
     * @param name the name of the slicing
     * @return a new slice type with the given {@code name}
     */
    public static SliceType customType(final String name) {
        return new SliceType(Kind.CUSTOM, name);
    }

    Kind kind;

    String name;

    public boolean isClassType() {
        return this.kind == Kind.CLASS;
    }

    public boolean isSliceType() {
        return this.kind == Kind.CUSTOM;
    }

    public String displayString() {
        return this.name;
    }

    @Override
    public int compareTo(final SliceType other) {
        return COMPARATOR.compare(this, other);
    }
}
