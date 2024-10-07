package org.y1000.util;

// Consumer<T> should enough, why it's here?
@Deprecated
@FunctionalInterface
public interface UnaryAction<T> {

    void invoke(T t);
}
