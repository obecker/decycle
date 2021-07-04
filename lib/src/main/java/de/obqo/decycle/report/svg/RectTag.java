package de.obqo.decycle.report.svg;

public class RectTag extends SvgContainerTag<RectTag> implements ICoordAttr<RectTag> {

    public RectTag() {
        super("rect");
    }

    public RectTag width(final double width) {
        return attr("width", width);
    }

    public RectTag height(final double height) {
        return attr("height", height);
    }

    public RectTag widthAndHeight(final double width, final double height) {
        return width(width).height(height);
    }

    public RectTag rx(final double rx) {
        return attr("rx", rx);
    }
}
