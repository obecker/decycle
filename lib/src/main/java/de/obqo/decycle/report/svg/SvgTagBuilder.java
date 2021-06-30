package de.obqo.decycle.report.svg;

public class SvgTagBuilder {

    public static SvgTag svg() {
        return new SvgTag().attr("xmlns", "http://www.w3.org/2000/svg")
                .attr("xmlns:xlink", "http://www.w3.org/1999/xlink");
    }

    public static PathTag path() {
        return new PathTag();
    }

    public static GenericSvgTag linearGradient() {
        return new GenericSvgTag("linearGradient");
    }

    public static StopTag stop() {
        return new StopTag();
    }

    public static GenericSvgTag clipPath() {
        return new GenericSvgTag("clipPath");
    }

    public static RectTag rect() {
        return new RectTag();
    }

    public static PolygonTag polygon() {
        return new PolygonTag();
    }

    public static MarkerTag marker() {
        return new MarkerTag();
    }

    public static GenericSvgTag g() {
        return new GenericSvgTag("g");
    }

    public static GenericSvgTag text() {
        return new GenericSvgTag("text");
    }

    public static GenericSvgTag defs() {
        return new GenericSvgTag("defs");
    }

    public static GenericSvgTag use() {
        return new GenericSvgTag("use");
    }
}
