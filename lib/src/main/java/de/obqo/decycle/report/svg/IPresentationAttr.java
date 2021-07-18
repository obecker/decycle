package de.obqo.decycle.report.svg;

import j2html.tags.IInstance;

public interface IPresentationAttr<T extends SvgContainerTag<T>> extends IInstance<T> {

    default T clipPath(final String clipPath) {
        return self().attr("clip-path", clipPath);
    }

    default T color(final String color) {
        return self().attr("color", color);
    }

    default T fill(final String fill) {
        return self().attr("fill", fill);
    }

    default T fillOpacity(final double opacity) {
        return self().attr("fill-opacity", opacity);
    }

    default T fontFamily(final String fontFamily) {
        return self().attr("font-family", fontFamily);
    }

    default T fontWeight(final String fontWeight) {
        return self().attr("font-weight", fontWeight);
    }

    default T fontSize(final String fontSize) {
        return self().attr("font-size", fontSize);
    }

    default T markerEnd(final String markerEnd) {
        return self().attr("marker-end", markerEnd);
    }

    default T stroke(final String stroke) {
        return self().attr("stroke", stroke);
    }

    default T strokeWidth(final double width) {
        return self().attr("stroke-width", width);
    }

    default T textAnchor(final String textAnchor) {
        return self().attr("text-anchor", textAnchor);
    }

    default T textRendering(final String textRendering) {
        return self().attr("text-rendering", textRendering);
    }
}
