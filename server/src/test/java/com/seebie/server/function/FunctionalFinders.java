package com.seebie.server.function;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.Arrays.asList;

public class FunctionalFinders {


    /**
     * Use this when having more than one element in the stream would be an error.
     *
     * Optional&lt;User&gt; resultUser = users.stream()
     *      .filter(user -&gt; user.getId() == 100)
     *      .collect(findOne());
     *
     * @param <T> Type
     * @return Collection
     */
    public static <T> Collector<T, ?, Optional<T>> toOne() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalArgumentException(STR."Must have zero or one element, found \{list.size()}");
                    }
                    return list.size() == 1 ? Optional.of(list.get(0)) : Optional.empty();
                }
        );
    }

    /**
     * Use this when not having exactly one element in the stream would be an error.
     *
     * Usage:
     *
     * User resultUser = users.stream()
     *      .filter(user -&gt; user.getId() == 100)
     *      .collect(findExactlyOne());
     *
     * @param messages optional message to show if not finding the expected number of elements
     * @param <T> Type
     * @return exactly one element.
     */
    public static <T> Collector<T, ?, T> toExactlyOne(String... messages) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        String m = STR."Must have exactly one element, found \{list.size()}. \{join(", ", asList(messages))}";
                        throw new IllegalArgumentException(m);
                    }
                    return list.get(0);
                }
        );
    }
}
