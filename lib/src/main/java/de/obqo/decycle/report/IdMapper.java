package de.obqo.decycle.report;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;

/**
 * Helper class for generating {@link String} ids for arbitrary objects of type {@code T}. For equal objects the same id
 * will be generated.
 *
 * @param <T>
 */
@RequiredArgsConstructor
class IdMapper<T> {

    private final Map<T, String> idMap = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    /**
     * Common prefix for the ids. Should be an allowed HTML {code id} value.
     */
    private final String idPrefix;

    /**
     * @param obj an object
     * @return a unique {@code id} for the given {@code obj}
     */
    String getId(final T obj) {
        return this.idMap.computeIfAbsent(obj, __ -> this.idPrefix + this.idCounter.incrementAndGet());
    }
}
