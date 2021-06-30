package de.obqo.decycle.report.svg;

import java.util.HashMap;
import java.util.Map;

import j2html.attributes.Attribute;

/**
 * Dynamic {@code id} attribute that will get a number appended to its value in order to achieve uniqueness within a
 * HTML document. To be used for generated SVG that may appear multiple times in the same document.
 */
public class DynIdAttribute extends Attribute {

    private static final ThreadLocal<Map<String, Integer>> idMap = ThreadLocal.withInitial(HashMap::new);

    private DynIdAttribute(final String value) {
        super("id", value);
    }

    /**
     * Removes all {@code id} counters. May be invoked before rendering a new HTML document.
     */
    public static void resetDynIds() {
        idMap.set(new HashMap<>());
    }

    /**
     * Creates a dynamic {@code id} attribute that can be passed to {@link j2html.tags.Tag#attr(Attribute)}.
     *
     * @param value the (base) value
     * @return the attribute (which renders to {@code id="value<n>"})
     */
    public static DynIdAttribute dynId(final String value) {
        final int counter = idMap.get().getOrDefault(value, 0) + 1;
        idMap.get().put(value, counter);
        return new DynIdAttribute(value + counter);
    }

    /**
     * Creates a reference (or a target anchor) for the given {@code id} (for example for the target of an xlink)
     *
     * @param value the (base) value of the target {@code id} attribute
     * @return the {@code id} reference: {@code "#value<n>"}
     */
    public static String ref(final String value) {
        return "#" + value + idMap.get().get(value);
    }

    /**
     * Creates an {@code url()} CSS function call using the reference for the given {@code id} as parameter (to be used
     * for example in SVG presentation attributes)
     *
     * @param value the (base) value of the target {@code id} attribute
     * @return the {@code url()} function: {@code "url(#value<n>)"}
     */
    public static String url(final String value) {
        return "url(#" + value + idMap.get().get(value) + ")";
    }
}
