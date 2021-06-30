package de.obqo.decycle.report.svg;

import static de.obqo.decycle.report.svg.NumberFormatter.formatNumber;

public class PathBuilder {

    public static PathBuilder from(final double x, final double y) {
        return new PathBuilder().absMoveTo(x, y);
    }

    private final StringBuilder path;

    private PathBuilder() {
        this.path = new StringBuilder();
    }

    @Override
    public String toString() {
        return this.path.toString();
    }

    public PathBuilder absMoveTo(final double x, final double y) {
        return line('M', x, y);
    }

    public PathBuilder relMoveTo(final double dx, final double dy) {
        return line('m', dx, dy);
    }

    public PathBuilder close() {
        this.path.append('z');
        return this;
    }

    public PathBuilder absVerticalLineTo(final double y) {
        return line('V', y);
    }

    public PathBuilder relVerticalLineTo(final double dy) {
        return line('v', dy);
    }

    public PathBuilder absHorizontalLineTo(final double x) {
        return line('H', x);
    }

    public PathBuilder relHorizontalLineTo(final double dx) {
        return line('h', dx);
    }

    private PathBuilder line(final char command, final double arg) {
        this.path.append(command).append(formatNumber(arg));
        return this;
    }

    public PathBuilder absLineTo(final double x, final double y) {
        return line('L', x, y);
    }

    public PathBuilder relLineTo(final double dx, final double dy) {
        return line('l', dx, dy);
    }

    private PathBuilder line(final char command, final double x, final double y) {
        this.path.append(command).append(formatNumber(x)).append(' ').append(formatNumber(y));
        return this;
    }

    public PathBuilder absArc(final double rx, final double ry, final double angle, final boolean largeArc,
            final boolean clockwise, final double x, final double y) {
        return arc('A', rx, ry, angle, largeArc, clockwise, x, y);
    }

    public PathBuilder relArc(final double rx, final double ry, final double angle, final boolean largeArc,
            final boolean clockwise, final double dx, final double dy) {
        return arc('a', rx, ry, angle, largeArc, clockwise, dx, dy);
    }

    private PathBuilder arc(final char command, final double rx, final double ry, final double angle,
            final boolean largeArc, final boolean clockwise, final double x, final double y) {
        this.path.append(command)
                .append(formatNumber(rx)).append(' ').append(formatNumber(ry)).append(' ')
                .append(formatNumber(angle)).append(' ')
                .append(largeArc ? 1 : 0).append(' ')
                .append(clockwise ? 1 : 0).append(' ')
                .append(formatNumber(x)).append(' ').append(formatNumber(y));
        return this;
    }

}
