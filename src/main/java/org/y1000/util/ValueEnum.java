package org.y1000.util;

@SuppressWarnings("ALL")
public interface ValueEnum {

    int value();

    static <E extends Enum<E> & ValueEnum> E getTypeOrThrow(E [] values, int v) {
        for (var value : values) {
            if (value.value() == v)
                return (E) value;
        }
        throw new IllegalArgumentException("Unknown value " + v);
    }

}
