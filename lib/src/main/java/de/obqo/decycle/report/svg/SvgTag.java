package de.obqo.decycle.report.svg;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SvgTag extends SvgContainerTag<SvgTag> {

    public SvgTag() {
        super("svg");
    }

    public SvgTag viewBox(final double x, final double y, final double width, final double height) {
        return attr("viewBox",
                Stream.of(x, y, width, height).map(NumberFormatter::formatNumber).collect(Collectors.joining(" ")));
    }

    public SvgTag widthAndHeight(final int width, final int height) {
        return attr("width", width).attr("height", height);
    }
}
