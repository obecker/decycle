package de.obqo.decycle.model;

import java.util.function.Predicate;

public interface NodeFilter extends Predicate<Node> {

    NodeFilter ALL = node -> true;
}
