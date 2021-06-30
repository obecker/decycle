package de.obqo.decycle.report.svg;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PolygonTag extends SvgContainerTag<PolygonTag> {

    public PolygonTag() {
        super("polygon");
    }

    public PolygonTag points(final double... points) {
        return attr("points",
                Arrays.stream(points).mapToObj(NumberFormatter::formatNumber).collect(Collectors.joining(" ")));
    }
}
