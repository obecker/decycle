package de.obqo.decycle.report.svg;

import j2html.tags.IInstance;

public interface ICoordAttr<T extends SvgContainerTag<T>> extends IInstance<T> {

    default T x(final double x) {
        return self().attr("x", x);
    }

    default T y(final double y) {
        return self().attr("y", y);
    }
}
