package org.y1000.message;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueEnumTest {

    public enum TestEnum implements ValueEnum {
        EA(1),
        EC(3);
        private final int v;

        TestEnum(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }

    @org.junit.jupiter.api.Test
    void getEnumByInt() {
        assertEquals(TestEnum.EA, ValueEnum.fromValueOrThrow(TestEnum.values(), 1));
        assertEquals(TestEnum.EC, ValueEnum.fromValueOrThrow(TestEnum.values(), 3));
        assertThrows(IllegalArgumentException.class, () -> ValueEnum.fromValueOrThrow(TestEnum.values(),0));
        assertThrows(IllegalArgumentException.class, () -> ValueEnum.fromValueOrThrow(TestEnum.values(),2));
    }

}