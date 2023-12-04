package org.y1000.message;

public interface ValueEnum {

    int value();

    static <E extends Enum<E> & ValueEnum> E fromValueOrThrow(ValueEnum[] values, int v) {
        for (var value : values) {
            if (value.value() == v)
                return (E)value;
        }
        throw new IllegalArgumentException("Unknown value " + v);
    }
}
