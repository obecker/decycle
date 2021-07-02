package de.obqo.decycle.report.svg;

import static de.obqo.decycle.report.svg.NumberFormatter.formatNumber;

import j2html.tags.ContainerTag;

class SvgContainerTag<T extends SvgContainerTag<T>> extends ContainerTag<T> implements IPresentationAttr<T> {

    public SvgContainerTag(final String tagName) {
        super(tagName);
    }

    public T attr(final String name, final double value) {
        return attr(name, formatNumber(value));
    }

    public T condAttr(final boolean condition, final String name, final double value) {
        return condAttr(condition, name, formatNumber(value));
    }
}
