package org.y1000.util;

@FunctionalInterface
public interface Action<T> {
    void act(T t);
}
