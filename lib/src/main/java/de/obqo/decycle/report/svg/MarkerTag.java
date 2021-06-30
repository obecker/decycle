package de.obqo.decycle.report.svg;

public class MarkerTag extends SvgContainerTag<MarkerTag> {

    public MarkerTag() {
        super("marker");
    }

    public MarkerTag markerWidth(final double markerWidth) {
        return attr("markerWidth", markerWidth);
    }

    public MarkerTag markerHeight(final double markerHeight) {
        return attr("markerHeight", markerHeight);
    }

    public MarkerTag refX(final double x) {
        return attr("refX", x);
    }

    public MarkerTag refY(final double y) {
        return attr("refY", y);
    }

    public MarkerTag orient(final String orient) {
        return attr("orient", orient);
    }

    public MarkerTag markerUnits(final String units) { // enum??
        return attr("markerUnits", units);
    }
}
