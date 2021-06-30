package de.obqo.decycle.report.svg;

public class StopTag extends SvgContainerTag<StopTag> {

    public StopTag() {
        super("stop");
    }

    public StopTag offset(final double offset) {
        return attr("offset", offset);
    }

    public StopTag stopColor(final String color) {
        return attr("stop-color", color);
    }

    public StopTag stopOpacity(final double opacity) {
        return attr("stop-opacity", opacity);
    }
}
