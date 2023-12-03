package org.y1000.message;

public interface ValueEnum <E extends Enum<E>> {

    int value();

    static <E extends Enum<E>> E fromValue(ValueEnum<E>[] values, int v) {
        for (var value : values) {
            if (value.value() == v)
                return (E)value;
        }
        return null;
    }
}
