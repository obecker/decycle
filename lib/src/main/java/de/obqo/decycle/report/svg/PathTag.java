package de.obqo.decycle.report.svg;

public class PathTag extends SvgContainerTag<PathTag> {

    public PathTag() {
        super("path");
    }

    public PathTag d(final String def) {
        return attr("d", def);
    }

    public PathTag d(final PathBuilder path) {
        return attr("d", path);
    }
}
