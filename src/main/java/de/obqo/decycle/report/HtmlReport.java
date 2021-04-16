package de.obqo.decycle.report;

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
import static j2html.TagCreator.html;
import static j2html.TagCreator.i;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;
import static j2html.TagCreator.scriptWithInlineFile_min;
import static j2html.TagCreator.span;
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
import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.graph.Network;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public class HtmlReport {

    private static final SliceComparator SLICE_COMPARATOR = new SliceComparator();

    public void writeReport(final Appendable out, final Graph graph, final List<Constraint.Violation> violations)
            throws IOException {

        final var violationDiv = getViolationDiv(violations);
        final var sliceSections = graph.slices().stream()
                .filter(Predicate.not(Node.CLASS::equals))
                .sorted(SLICE_COMPARATOR)
                .map(slice -> renderSliceSection(graph, violations, slice));

        html().withLang("en").with(
                head(
                        meta().withCharset("UTF-8"),
                        title("Decycle Report"),
                        // https://getbootstrap.com/docs/4.6/getting-started/introduction/
                        link().withRel("stylesheet")
                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css")
                                .attr("integrity",
                                        "sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l")
                                .attr("crossorigin", "anonymous"),
                        // https://icons.getbootstrap.com/#usage
                        link().withRel("stylesheet")
                                .withHref(
                                        "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"),
                        styleWithInlineFile_min("/report/custom.css"),
                        // https://code.jquery.com
                        script().withSrc("https://code.jquery.com/jquery-3.6.0.min.js")
                                .attr("integrity", "sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=")
                                .attr("crossorigin", "anonymous"),
                        scriptWithInlineFile_min("/report/custom.js")
                ),
                body(
                        div(
                                h1("Decycle Report").withClass("mb-4"),
                                violationDiv,
                                each(sliceSections)
                        ).withClass("container mx-auto p-3")
                )
        ).render(out);
        out.append('\n');
    }

    private DomContent getViolationDiv(final List<Constraint.Violation> violations) {
        return iff(!violations.isEmpty(),
                div().withClass("violations border rounded-lg pb-1 mb-3 alert-danger row").with(
                        h1().withClass("m-0 pt-2").with(i().withClass("bi bi-exclamation-triangle-fill")),
                        each(violations.stream()
                                .sorted(Comparator.comparing(Constraint.Violation::getSliceType, SLICE_COMPARATOR)
                                        .thenComparing(Constraint.Violation::getName))
                                .flatMap(this::getViolationDivColumns))));
    }

    private Stream<ContainerTag> getViolationDivColumns(final Constraint.Violation v) {
        return Stream.of(
                div().withClass("col-4 name mt-1").with(b(v.getName())),
                div().withClass("col-8 dependencies mt-1")
                        .with(v.getDependencies().stream().sorted().map(dependency ->
                                div(a(dependency.toString())
                                        .withHref("#" + v.getSliceType() + "-" + dependency.getFrom())
                                        .withClass("alert-link")))));
    }

    private DomContent renderSliceSection(final Graph graph, final List<Constraint.Violation> violations,
            final String slice) {
        final Map<String, Map<String, List<String>>> violationsIndex =
                violations.stream()
                        .filter(v -> v.getSliceType().equals(slice))
                        .flatMap(v -> v.getDependencies().stream()
                                .map(d -> entry(d.getFrom(), entry(d.getTo(), v.getName()))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue,
                                groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())))));

        final Network<Node, Edge> network = graph.slice(slice);

        return div().withClass("pt-2").with(
                h2(slice).withClass("slice mt-2"),
                dl().withClass("slices row border rounded-lg py-1").with(
                        network.nodes().stream()
                                .sorted()
                                .flatMap(node -> renderNodeTableRow(graph, network, violationsIndex, node))));
    }

    private Stream<DomContent> renderNodeTableRow(final Graph graph, final Network<Node, Edge> network,
            final Map<String, Map<String, List<String>>> violationsIndex,
            final Node node) {
        final Map<String, List<String>> fromViolations = violationsIndex.getOrDefault(node.getName(), Map.of());
        final String errorClass = fromViolations.isEmpty() ? "" : "error";

        return Stream.of(
                dt().withClasses("col-sm-4", "border-top", "py-1", "text-truncate", errorClass)
                        .withTitle(node.getName())
                        .with(a(node.getName()).withName(node.getType() + "-" + node.getName())),
                dd().withClasses("col-sm-8", "border-top", "pt-1", errorClass)
                        .with(ul().withClass("references list-unstyled mb-0")
                                .with(renderOutEdgesList(graph, network, node, fromViolations))));
    }

    private Stream<DomContent> renderOutEdgesList(final Graph graph, final Network<Node, Edge> network, final Node node,
            final Map<String, List<String>> fromViolations) {
        return network.outEdges(node)
                .stream()
                .sorted()
                .map(edge -> {
                    final List<String> toViolations = fromViolations.getOrDefault(edge.getTo().getName(), List.of());
                    return li()
                            .withClasses("pb-1", toViolations.isEmpty() ? "" : "error")
                            .with(
                                    a(edge.getTo().getName() + " ").withClass("mr-2"),
                                    iff(!toViolations.isEmpty(),
                                            span(
                                                    i().withClass("bi bi-exclamation-triangle-fill"),
                                                    text(toViolations.stream().collect(joining(", ", " (", ")"))))),
                                    ul().withClass("class-references list-unstyled")
                                            .with(graph.containingClassEdges(edge)
                                                    .stream()
                                                    .sorted()
                                                    .map(classEdge -> li(
                                                            span().withClass("class-node")
                                                                    .withData("name", classEdge.getFrom().getName())
                                                                    .withText(classEdge.getFrom().getName()),
                                                            rawHtml(" &rarr;&nbsp;"),
                                                            span().withClass("class-node")
                                                                    .withData("name", classEdge.getTo().getName())
                                                                    .withText(classEdge.getTo().getName())))));
                });
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
