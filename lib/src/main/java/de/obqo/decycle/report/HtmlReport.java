package de.obqo.decycle.report;

import static de.obqo.decycle.report.MarkupReader.base64FromFile_min;
import static de.obqo.decycle.report.MarkupReader.rawHtmlWithInlineFile;
import static de.obqo.decycle.report.MarkupReader.rawHtmlWithInlineFile_min;
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

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.model.Node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import j2html.TagCreator;
import j2html.rendering.FlatHtml;
import j2html.rendering.IndentedHtml;
import j2html.tags.DomContent;
import j2html.tags.specialized.ScriptTag;
import j2html.tags.specialized.StyleTag;

public class HtmlReport {

    private static final SliceComparator SLICE_COMPARATOR = new SliceComparator();

    public void writeReport(final Graph graph, final List<Constraint.Violation> violations, final Appendable out,
            final String title, final boolean minify) {

        final var sliceSections = graph.sliceTypes().stream()
                .filter(Predicate.not(Node.CLASS::equals))
                .sorted(SLICE_COMPARATOR)
                .map(sliceType -> renderSliceSection(graph, violations, sliceType));

        final var html = html().withLang("en").with(
                head(
                        meta().withCharset("UTF-8"),
                        meta().withName("viewport")
                                .withContent("width=device-width, initial-scale=1, shrink-to-fit=no"),
                        title((title != null ? title + " - " : "") + "Decycle Report"),
                        link().withHref("data:image/svg+xml;base64," + base64FromFile_min("/report/icon.svg"))
                                .withRel("icon").withType("image/svg+xml"),
                        // https://getbootstrap.com/docs/4.6/getting-started/introduction/
                        link().withRel("stylesheet")
                                .withHref(
                                        "https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css")
                                .attr("integrity",
                                        "sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l")
                                .attr("crossorigin", "anonymous"),
                        // https://icons.getbootstrap.com/#usage
                        link().withRel("stylesheet")
                                .withHref(
                                        "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css"),
                        inlineStyle(minify, "/report/custom.css"),
                        // https://code.jquery.com
                        script().withSrc("https://code.jquery.com/jquery-3.6.0.min.js")
                                .attr("integrity", "sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=")
                                .attr("crossorigin", "anonymous"),
                        inlineScript(minify, "/report/custom.js")
                ),
                body(
                        div().withClass("container-fluid p-3").with(
                                div().withClass("mx-2").with(
                                        inlineMarkup(minify, "/report/logo.svg"),
                                        hr().withClass("mt-1"),
                                        h1().withClass("mb-3")
                                                .withText("Violation Report" + (title != null ? " for " + title : "")),
                                        getViolationDiv(violations),
                                        each(sliceSections),
                                        hr(),
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

        try {
            html.render(minify ? FlatHtml.into(out) : IndentedHtml.into(out));
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
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

    private DomContent getViolationDiv(final List<Constraint.Violation> violations) {
        return violations.isEmpty()
                ? div().withClass("violations border rounded-lg pb-1 mb-3 alert-success row").with(
                h1().withClass("m-0 pt-2").with(i().withClass("bi bi-check-circle-fill")),
                div().withClass("col-12 mt-1").with(b("No violations found")))
                : div().withClass("violations border rounded-lg pb-1 mb-3 alert-danger row").with(
                h1().withClass("m-0 pt-2").with(i().withClass("bi bi-exclamation-triangle-fill")),
                each(violations.stream()
                        .sorted(Comparator.comparing(Constraint.Violation::getSliceType, SLICE_COMPARATOR)
                                .thenComparing(Constraint.Violation::getName))
                        .flatMap(this::getViolationDivColumns)));
    }

    private Stream<DomContent> getViolationDivColumns(final Constraint.Violation v) {
        return Stream.of(
                div().withClass("col-4 name mt-1").with(b(replaceArrows(v.getName()))),
                div().withClass("col-8 dependencies mt-1")
                        .with(v.getDependencies().stream().map(dependency ->
                                div(a(dependency.toString())
                                        .withHref("#" + v.getSliceType() + "-" + dependency.getFrom())
                                        .withClass("alert-link")))));
    }

    private String replaceArrows(final String name) {
        return name.replace("->", "→").replace("=>", "⇨");
    }

    private DomContent renderSliceSection(final Graph graph, final List<Constraint.Violation> violations,
            final String sliceType) {
        final Map<String, Map<String, List<String>>> violationsIndex =
                violations.stream()
                        .filter(v -> v.getSliceType().equals(sliceType))
                        .flatMap(v -> v.getDependencies().stream()
                                .map(d -> entry(d.getFrom(), entry(d.getTo(), v.getName()))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue,
                                groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())))));

        final Slicing slicing = graph.slicing(sliceType);

        return div().withClass("pt-2").with(
                h2(sliceType).withClass("slice text-capitalize mt-2"),
                dl().withClass("slices row border rounded-lg py-1").with(
                        slicing.nodes().stream()
                                .sorted()
                                .flatMap(node -> renderNodeTableRow(graph, slicing, violationsIndex, node))));
    }

    private Stream<DomContent> renderNodeTableRow(final Graph graph, final Slicing slicing,
            final Map<String, Map<String, List<String>>> violationsIndex,
            final Node node) {
        final Map<String, List<String>> fromViolations = violationsIndex.getOrDefault(node.getName(), Map.of());
        final String errorClass = fromViolations.isEmpty() ? "" : "error";

        return Stream.of(
                dt().withClasses("col-sm-4", "border-top", "py-1", "text-truncate", errorClass)
                        .withTitle(node.getName())
                        .with(a(node.getName()).withId(node.getType() + "-" + node.getName())),
                dd().withClasses("col-sm-8", "border-top", "py-1", "mb-0", errorClass)
                        .with(ul().withClass("references list-unstyled mb-0")
                                .with(renderOutEdgesList(graph, slicing, node, fromViolations))));
    }

    private Stream<DomContent> renderOutEdgesList(final Graph graph, final Slicing slicing, final Node node,
            final Map<String, List<String>> fromViolations) {
        return slicing.outEdges(node)
                .stream()
                .sorted()
                .map(edge -> {
                    final List<String> toViolations = fromViolations.getOrDefault(edge.getTo().getName(), List.of());
                    final boolean hasViolations = !toViolations.isEmpty();
                    return li()
                            .withClasses("pb-1", iff(hasViolations, "error"))
                            .with(
                                    a().withClass("mr-2 toggle-display").with(
                                            i().withClasses("bi", "bi-arrows-expand", iff(hasViolations, "hidden"))
                                                    .withTitle("Show class dependencies"),
                                            i().withClasses("bi", "bi-arrows-collapse", iff(!hasViolations, "hidden"))
                                                    .withTitle("Hide class dependencies")),
                                    a().withHref("#" + slicing.getSliceType() + "-" + edge.getTo().getName())
                                            .withClass("mr-2")
                                            .with(wrap(edge.isIgnored(), TagCreator::del,
                                                    text(edge.getTo().getName()))),
                                    iff(hasViolations, span(
                                            text(" "),
                                            i().withClass("bi bi-exclamation-triangle-fill"),
                                            text(toViolations.stream().collect(joining(", ", " (", ")"))))),
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

    private static class SliceComparator implements Comparator<String> {

        @Override
        public int compare(final String slice1, final String slice2) {
            // Put the PACKAGE slice always at the first position
            if (Node.PACKAGE.equals(slice1)) {
                return -1;
            }
            if (Node.PACKAGE.equals(slice2)) {
                return 1;
            }
            return slice1.compareTo(slice2);
        }
    }
}
