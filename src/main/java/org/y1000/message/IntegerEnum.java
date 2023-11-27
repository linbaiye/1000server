package org.y1000.message;

public interface IntegerEnum <E extends Enum<E>> {

    int value();

    static <E extends Enum<E>> E fromValue(IntegerEnum<E>[] values, int v) {
        for (var value : values) {
            if (value.value() == v)
                return (E)value;
        }
        return null;
    }
}
