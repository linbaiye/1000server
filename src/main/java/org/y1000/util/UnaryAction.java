package org.y1000.util;

@FunctionalInterface
public interface UnaryAction<T> {

    void invoke(T t);
}
