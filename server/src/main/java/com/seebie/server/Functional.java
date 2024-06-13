package com.seebie.server;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.Arrays.asList;

public class Functional {

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void apply(T t) throws Exception;
    }

    public static <T, R> Function<T, R> uncheck(CheckedFunction<T, R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Consumer<T> uncheck(CheckedConsumer<T> checkedConsumer) {
        return t -> {
            try {
                checkedConsumer.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


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
                        throw new IllegalStateException(STR."Must have zero or one element, found \{list.size()}");
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
                        String m = STR."Must have exactly one element, found \{list}. \{join(", ", asList(messages))}";
                        throw new IllegalStateException(m);
                    }
                    return list.get(0);
                }
        );
    }
}
