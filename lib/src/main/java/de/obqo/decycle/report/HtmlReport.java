package de.obqo.decycle.report;

import static de.obqo.decycle.report.MarkupReader.base64FromFile_min;
import static de.obqo.decycle.report.MarkupReader.rawHtmlWithInlineFile;
import static de.obqo.decycle.report.MarkupReader.rawHtmlWithInlineFile_min;
import static de.obqo.decycle.report.svg.DynIdAttribute.dynId;
import static de.obqo.decycle.report.svg.DynIdAttribute.dynRef;
import static de.obqo.decycle.report.svg.DynIdAttribute.dynUrl;
import static de.obqo.decycle.report.svg.DynIdAttribute.resetDynIds;
import static de.obqo.decycle.report.svg.PathBuilder.from;
import static de.obqo.decycle.report.svg.SvgTagBuilder.clipPath;
import static de.obqo.decycle.report.svg.SvgTagBuilder.defs;
import static de.obqo.decycle.report.svg.SvgTagBuilder.g;
import static de.obqo.decycle.report.svg.SvgTagBuilder.linearGradient;
import static de.obqo.decycle.report.svg.SvgTagBuilder.marker;
import static de.obqo.decycle.report.svg.SvgTagBuilder.path;
import static de.obqo.decycle.report.svg.SvgTagBuilder.rect;
import static de.obqo.decycle.report.svg.SvgTagBuilder.stop;
import static de.obqo.decycle.report.svg.SvgTagBuilder.svg;
import static de.obqo.decycle.report.svg.SvgTagBuilder.text;
import static de.obqo.decycle.report.svg.SvgTagBuilder.use;
import static j2html.TagCreator.a;
import static j2html.TagCreator.b;
import static j2html.TagCreator.body;
import static j2html.TagCreator.dd;
import static j2html.TagCreator.div;
import static j2html.TagCreator.dl;
import static j2html.TagCreator.dt;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.head;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.html;
import static j2html.TagCreator.i;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;
import static j2html.TagCreator.scriptWithInlineFile;
import static j2html.TagCreator.scriptWithInlineFile_min;
import static j2html.TagCreator.span;
import static j2html.TagCreator.styleWithInlineFile;
import static j2html.TagCreator.styleWithInlineFile_min;
import static j2html.TagCreator.text;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;
import static java.util.Map.entry;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import de.obqo.decycle.check.Constraint.Violation;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;
import de.obqo.decycle.report.svg.MarkerTag;
import de.obqo.decycle.report.svg.SvgTag;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import j2html.Config;
import j2html.TagCreator;
import j2html.rendering.FlatHtml;
import j2html.rendering.IndentedHtml;
import j2html.tags.DomContent;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.ScriptTag;
import j2html.tags.specialized.StyleTag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HtmlReport {

    private final boolean minify;

    private final IdMapper<Edge> edgeIds = new IdMapper<>("e");

    public void writeReport(final Graph graph, final List<Violation> violations, final Appendable out,
            final String title) {

        resetDynIds();

        final HtmlTag html = buildHtml(graph, violations, title);

        final Config config = Config.defaults().withTextEscaper(ImprovedTextEscaper::escape);
        try {
            html.render(this.minify ? FlatHtml.into(out, config) : IndentedHtml.into(out, config));
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private HtmlTag buildHtml(final Graph graph, final List<Violation> violations, final String title) {
        final var sliceSections = graph.sliceTypes().stream()
                .filter(Predicate.not(SliceType::isClassType))
                .sorted()
                .map(sliceType -> buildSliceSection(graph, violations, sliceType));

        return html().withLang("en").with(
                head(
                        meta().withCharset("UTF-8"),
                        meta().withName("viewport")
                                .withContent("width=device-width, initial-scale=1, shrink-to-fit=no"),
                        meta().withName("referrer").withContent("no-referrer"),
                        title((title != null ? title + " - " : "") + "Decycle Report"),
                        link().withHref("data:image/svg+xml;base64," + base64FromFile_min("/report/icon.svg"))
                                .withRel("icon").withType("image/svg+xml"),
                        // https://getbootstrap.com/docs/4.6/getting-started/introduction/
                        link().withRel("stylesheet")
                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css")
                                .attr("integrity",
                                        "sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l")
                                .attr("crossorigin", "anonymous"),
                        // https://icons.getbootstrap.com/#usage
                        link().withRel("stylesheet")
                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css")
                                .attr("integrity",
                                        "sha256-PDJQdTN7dolQWDASIoBVrjkuOEaI137FI15sqI3Oxu8=")
                                .attr("crossorigin", "anonymous"),
                        inlineStyle(this.minify, "/report/custom.css"),
                        // https://code.jquery.com
                        script().withSrc("https://code.jquery.com/jquery-3.6.0.min.js")
                                .attr("integrity", "sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=")
                                .attr("crossorigin", "anonymous"),
                        // https://cdnjs.com/libraries/svg.js (required by tooltipster)
                        script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/svg.js/3.1.1/svg.min.js")
                                .attr("integrity",
                                        "sha512-Aj0P6wguH3GVlCfbvTyMM90Zq886ePyMEYlZooRfx+3wcSYyUa6Uv4iAjoJ7yiWdKamqQzKp7yr/TkMQ8EEWbQ==")
                                .attr("crossorigin", "anonymous"),
                        // https://www.jsdelivr.com/package/npm/tooltipster
                        script().withSrc("https://cdn.jsdelivr.net/npm/tooltipster@4.2.8/dist/js/tooltipster.bundle.min.js")
                                .attr("integrity", "sha256-v8akIv8SCqn5f3mbVB7vEWprIizxPh6oV0yhao/dbB4=")
                                .attr("crossorigin", "anonymous"),
                        script().withSrc("https://cdn.jsdelivr.net/npm/tooltipster@4.2.8/dist/js/plugins/tooltipster/SVG/tooltipster-SVG.min.js")
                                .attr("integrity", "sha256-b9JNfGq08bjI5FVdN3ZhjWBSRsOyF6ucACQwlvgVEU4=")
                                .attr("crossorigin", "anonymous"),
                        link().withRel("stylesheet")
                                .withHref("https://cdn.jsdelivr.net/npm/tooltipster@4.2.8/dist/css/tooltipster.bundle.min.css")
                                .attr("integrity", "sha256-Qc4lCfqZWYaHF5hgEOFrYzSIX9Rrxk0NPHRac+08QeQ=")
                                .attr("crossorigin", "anonymous"),
                        inlineScript(this.minify, "/report/custom.js")
                ),
                body(
                        div().withClass("container-fluid p-3").with(
                                div().withClass("mx-2").with(
                                        inlineMarkup(this.minify, "/report/logo.svg"),
                                        hr().withClass("mt-1"),
                                        h1().withClass("mb-3")
                                                .withText("Violation Report" + (title != null ? " for " + title : "")),
                                        buildViolationDiv(violations),
                                        div().withClass("row").with(each(violations, this::buildViolationImage)),
                                        each(sliceSections),
                                        hr().withClass("row"),
                                        div().withClass("footer small").with(
                                                text("Generated by "),
                                                a().withHref("https://github.com/obecker/decycle")
                                                        .withTarget("decycle")
                                                        .withText("Decycle"),
                                                text(" "),
                                                text(HtmlReport.class.getPackage().getImplementationVersion())
                                        )
                                )
                        )
                )
        );
    }

    private StyleTag inlineStyle(final boolean minify, final String path) {
        return minify ? styleWithInlineFile_min(path) : styleWithInlineFile(path);
    }

    private ScriptTag inlineScript(final boolean minify, final String path) {
        return minify ? scriptWithInlineFile_min(path) : scriptWithInlineFile(path);
    }

    private DomContent inlineMarkup(final boolean minify, final String path) {
        return minify ? rawHtmlWithInlineFile_min(path) : rawHtmlWithInlineFile(path);
    }

    private DomContent buildViolationDiv(final List<Violation> violations) {
        return violations.isEmpty()
                ? div().withClass("violations border rounded-lg pb-1 mb-3 alert-success row").with(
                h1().withClass("m-0 pt-2").with(i().withClass("bi bi-check-circle-fill")),
                div().withClass("col-12 mt-1").with(b("No violations found")))
                : div().withClass("violations border rounded-lg pb-1 mb-3 alert-danger row").with(
                h1().withClass("m-0 pt-2").with(i().withClass("bi bi-exclamation-triangle-fill")),
                each(violations.stream().flatMap(this::buildViolationDivColumns)));
    }

    private Stream<DomContent> buildViolationDivColumns(final Violation v) {
        return Stream.of(
                div().withClass("col-4 name mt-1").with(b(violationTitle(v))),
                div().withClass("col-8 dependencies mt-1")
                        .with(v.getDependencies().stream().map(dependency ->
                                div(a(dependency.displayString())
                                        .withHref(nodeRef(dependency.getFrom()))
                                        .withClass("alert-link")))));
    }

    private String replaceArrows(final String name) {
        return name.replace("->", "→").replace("=>", "⇨");
    }

    private String nodeIdentifier(final Node node) {
        return nodeAnchor("", node);
    }

    private String nodeRef(final Node node) {
        return nodeAnchor("#", node);
    }

    private String nodeAnchor(final String prefix, final Node node) {
        return prefix + (node.getType().isSliceType() ? "s-" : "") + node.getType().getName() + "-" + node.getName();
    }

    private DomContent buildSliceSection(final Graph graph, final List<Violation> violations, final SliceType sliceType) {
        final Map<String, Map<String, List<String>>> violationsIndex =
                violations.stream()
                        .filter(v -> v.getSliceType().equals(sliceType))
                        .flatMap(v -> v.getDependencies().stream()
                                .map(d -> entry(d.getFrom().getName(), entry(d.getTo().getName(), v.getName()))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue,
                                groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())))));

        final Slicing slicing = graph.slicing(sliceType);

        return div().withClass("pt-2").with(
                h2(sliceType.displayString()).withClass("slice text-capitalize mt-2"),
                dl().withClass("slices row border rounded-lg py-1").with(
                        slicing.nodes().stream()
                                .sorted()
                                .flatMap(node -> buildNodeTableRow(graph, slicing, violationsIndex, node))),
                div().withClass("row").with(buildDependencyImage(slicing, sliceType.displayString()))
        );
    }

    private SvgTag buildViolationImage(final Violation violation) {
        return buildDependencyImage(violation.getViolatingSubgraph(), violationTitle(violation));
    }

    private String violationTitle(final Violation violation) {
        return capitalize(violation.getSliceType().displayString()) + " " + replaceArrows(violation.getName());
    }

    private String capitalize(final String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private SvgTag buildDependencyImage(final Slicing slicing, final String title) {
        final List<Node> nodeList = slicing.orderedNodes();
        final FontMetricsSupport metrics = FontMetricsSupport.get(this.minify);
        final int maxTextWidth = nodeList.stream().map(Node::getName).mapToInt(metrics::widthOf).max().orElse(0);
        final int verticalDistance = 420;
        final int boxWidth = maxTextWidth + 150;
        final int boxHeight = 200;

        final int arrowSpacing = 60;  // distance between in- and outgoing arcs
        final int arrowLength = 55;

        final int xShrinkFactor = 2;
        final int svgShrinkFactor = 8;

        final int totalPadding = 100;
        final int titleWidth = metrics.widthOf(title) * 11 / 10 + 100; // width * 1.1 for bold text
        final int titleHeight = 190;
        final int graphHeight = (nodeList.size() - 1) * verticalDistance + boxHeight + 2 * totalPadding;
        final int arcSpace = graphHeight / 2 / xShrinkFactor + arrowLength - totalPadding / xShrinkFactor;
        final int totalWidth = Math.max(boxWidth + 2 * (arcSpace + totalPadding), titleWidth);
        final int totalHeight = graphHeight + titleHeight;

        final int centerBox = totalWidth / 2;
        final int leftBox = centerBox - boxWidth / 2;
        final int rightBox = leftBox + boxWidth;

        final Map<Node, Integer> nodePositionMap = new HashMap<>();

        final String downColor = "black";
        final String upColor = "#721c24";
        return svg().widthAndHeight(totalWidth / svgShrinkFactor, totalHeight / svgShrinkFactor)
                .viewBox(0, 0, totalWidth, totalHeight)
                .withClass("dependency-graph border rounded mr-3").with(
                        defs().with(
                                arrowHeadMarker("ad", downColor, 83 - arrowLength, 50),
                                arrowHeadMarker("au", upColor, 83 - arrowLength, 50),
                                linearGradient().attr(dynId("lg")).attr("x2", 0).attr("y2", "100%").with(
                                        stop().offset(0).stopColor("#bbb").stopOpacity(0.1),
                                        stop().offset(1).stopOpacity(0.1)),
                                clipPath().attr(dynId("cl")).with(
                                        rect().widthAndHeight(boxWidth, boxHeight).rx(35)),
                                g().attr(dynId("box")).clipPath(dynUrl("cl")).with(
                                        rect().widthAndHeight(boxWidth, boxHeight).fill("#c6d8ec"),
                                        rect().widthAndHeight(boxWidth, boxHeight).fill(dynUrl("lg"))
                                )
                        ),
                        text().x(centerBox).y((titleHeight - 70) / 2.0 + 70).fill("#000").fontWeight("bold")
                                .withText(title),
                        path().d(from(0, titleHeight).relHorizontalLineTo(totalWidth)).stroke("#dee2e6").strokeWidth(8),
                        each(nodeList, (index, node) -> {
                            final int yPos = index * verticalDistance + totalPadding + titleHeight;
                            nodePositionMap.put(node, yPos);
                            final String text = node.getName();
                            final String hrefTarget = nodeRef(node);
                            return a().attr("xlink:href", hrefTarget).withHref(hrefTarget).withClasses("node")
                                    .attr("data-name", nodeClassName(node))
                                    .with(
                                            use().href(dynRef("box")).x(leftBox).y(yPos),
                                            text().x(centerBox + 5).y(150 + yPos).fill("#F1F1F1").fillOpacity(0.5)
                                                    .withText(text),
                                            text().x(centerBox).y(140 + yPos).fill("#000").withText(text));
                        }),
                        each(slicing.edges().stream().sorted().map(edge -> {
                            final int positionFrom = nodePositionMap.get(edge.getFrom());
                            final int positionTo = nodePositionMap.get(edge.getTo());
                            final String fromName = nodeClassName(edge.getFrom());
                            final String toName = nodeClassName(edge.getTo());
                            if (positionFrom < positionTo) {
                                final double fromY = positionFrom + (boxHeight + arrowSpacing) / 2.0;
                                final double rx = (positionTo - positionFrom) / 2.0 / xShrinkFactor;
                                final double ry = (positionTo - positionFrom - arrowSpacing) / 2.0;
                                final double dy = positionTo - positionFrom - arrowSpacing;
                                return each(
                                        path()
                                                .withClasses(fromName, toName, "edge", iff(edge.isIgnored(), "ignored"))
                                                .d(from(rightBox, fromY)
                                                        .relHorizontalLineTo(arrowLength)
                                                        .relArc(rx, ry, 180, true, true, 0, dy))
                                                .stroke(downColor).strokeWidth(arcWidth(edge))
                                                .markerEnd(dynUrl("ad")),
                                        path()
                                                .withClasses("tip")
                                                .withData("ref", this.edgeIds.getId(edge))
                                                .d(from(rightBox, fromY)
                                                        .relHorizontalLineTo(arrowLength)
                                                        .relArc(rx, ry, 180, true, true, 0, dy)
                                                        .relHorizontalLineTo(-arrowLength))
                                );
                            } else {
                                final double fromY = positionFrom + (boxHeight - arrowSpacing) / 2.0;
                                final double rx = (positionFrom - positionTo) / 2.0 / xShrinkFactor;
                                final double ry = (positionFrom - positionTo - arrowSpacing) / 2.0;
                                final double dy = positionTo - positionFrom + arrowSpacing;
                                return each(
                                        path()
                                                .withClasses(fromName, toName, "edge", iff(edge.isIgnored(), "ignored"))
                                                .d(from(leftBox, fromY)
                                                        .relHorizontalLineTo(-arrowLength)
                                                        .relArc(rx, ry, 180, true, true, 0, dy))
                                                .stroke(upColor).strokeWidth(arcWidth(edge))
                                                .markerEnd(dynUrl("au")),
                                        path()
                                                .withClasses("tip")
                                                .withData("ref", this.edgeIds.getId(edge))
                                                .d(from(leftBox, fromY)
                                                        .relHorizontalLineTo(-arrowLength)
                                                        .relArc(rx, ry, 180, true, true, 0, dy)
                                                        .relHorizontalLineTo(arrowLength)));
                            }
                        }))
                );
    }

    private MarkerTag arrowHeadMarker(final String id, final String downColor, final int refX, final int refY) {
        final String path = "M85.2 50 21.2 82a4.8 4.8 0 0 1-6.4-4.8l6.4-28.16-6.4-28.16a4.8 4.8 0 0 1 6.4-4.8z";
        return marker().attr(dynId(id)).markerWidth(100).markerHeight(100)
                .refX(refX).refY(refY)
                .orient("auto").markerUnits("userSpaceOnUse")
                .with(path().fill(downColor).d(path));
    }

    private static double arcWidth(final Edge edge) {
        // compute the arc width as a logarithmic value of the edge weight (number of class dependencies)
        // minimum weight is 1, which results in a minimum width of about 4
        // since the width/height of the SVG is 1/8 of the viewBox (see svgShrinkFactor), this results in 0.5px,
        // which should be neatly displayable on a 4K screen
        return Math.log1p(edge.getWeight()) * 6;
    }

    private static String nodeClassName(final Node node) {
        return "n-" + node.getName().replace('.', '-');
    }

    private Stream<DomContent> buildNodeTableRow(final Graph graph, final Slicing slicing,
            final Map<String, Map<String, List<String>>> violationsIndex,
            final Node node) {
        final Map<String, List<String>> fromViolations = violationsIndex.getOrDefault(node.getName(), Map.of());
        final String errorClass = fromViolations.isEmpty() ? "" : "error";

        return Stream.of(
                dt().withClasses("col-sm-4", "border-top", "py-1", "text-truncate", errorClass)
                        .withTitle(node.getName())
                        .with(a(node.getName()).withId(nodeIdentifier(node))),
                dd().withClasses("col-sm-8", "border-top", "py-1", "mb-0", errorClass)
                        .with(ul().withClass("references list-unstyled mb-0")
                                .with(buildOutEdgesList(graph, slicing, node, fromViolations))));
    }

    private Stream<DomContent> buildOutEdgesList(final Graph graph, final Slicing slicing, final Node node,
            final Map<String, List<String>> fromViolations) {
        return slicing.outEdges(node)
                .stream()
                .sorted()
                .map(edge -> {
                    final List<String> toViolations = fromViolations.getOrDefault(edge.getTo().getName(), List.of());
                    final boolean hasViolations = !toViolations.isEmpty();
                    return li().withId(this.edgeIds.getId(edge))
                            .withClasses("pb-1", iff(hasViolations, "error"))
                            .with(
                                    a().withClass("toggle-display").with(
                                            i().withClasses("bi", "bi-arrows-expand", iff(hasViolations, "hidden"))
                                                    .withTitle("Show class dependencies"),
                                            i().withClasses("bi", "bi-arrows-collapse", iff(!hasViolations, "hidden"))
                                                    .withTitle("Hide class dependencies")),
                                    a().withHref(nodeRef(edge.getTo()))
                                            .withClass("mr-2")
                                            .with(wrap(edge.isIgnored(), TagCreator::del,
                                                    text(edge.getTo().getName()))),
                                    iff(hasViolations, span(
                                            text(" "),
                                            i().withClass("bi bi-exclamation-triangle-fill"),
                                            text(toViolations.stream().collect(joining(", ", " ", ""))))),
                                    ul().withClass("class-references list-unstyled mb-1")
                                            .with(graph.containingClassEdges(edge)
                                                    .stream()
                                                    .sorted()
                                                    .map(classEdge -> li(wrap(classEdge.isIgnored(), TagCreator::del,
                                                            span().withClass("class-node")
                                                                    .withData("name", classEdge.getFrom().getName())
                                                                    .withText(classEdge.getFrom().getName()),
                                                            rawHtml(" &rarr;&nbsp;"),
                                                            span().withClass("class-node")
                                                                    .withData("name", classEdge.getTo().getName())
                                                                    .withText(classEdge.getTo().getName()))))));
                });
    }

    private static DomContent wrap(final boolean condition, final Function<DomContent[], DomContent> wrapper,
            final DomContent... contents) {
        return condition ? wrapper.apply(contents) : each(contents);
    }
}
