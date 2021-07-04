package de.obqo.decycle.report.svg;

public class UseTag extends SvgContainerTag<UseTag> implements ICoordAttr<UseTag> {

    public UseTag() {
        super("use");
    }

    public UseTag href(final String href) {
        return attr("xlink:href", href).attr("href", href);
    }
}
