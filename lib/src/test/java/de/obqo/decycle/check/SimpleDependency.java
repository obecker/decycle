package de.obqo.decycle.check;

import de.obqo.decycle.model.Edge;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class SimpleDependency {

    String from;
    String to;

    public SimpleDependency(final Edge edge) {
        this(edge.getFrom().getName(), edge.getTo().getName());
    }

    public static SimpleDependency d(final String from, final String to) {
        return new SimpleDependency(from, to);
    }

    static List<SimpleDependency> dependenciesIn(final List<Constraint.Violation> violations) {
        return violations.stream()
                .flatMap(v -> v.getDependencies().stream())
                .map(SimpleDependency::new)
                .collect(Collectors.toList());
    }
}
