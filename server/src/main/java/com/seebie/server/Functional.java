package com.seebie.server;

import java.util.function.Consumer;
import java.util.function.Function;

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
}
