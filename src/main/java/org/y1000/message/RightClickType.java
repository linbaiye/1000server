package org.y1000.message;


public enum RightClickType implements ValueEnum {
    INVENTORY(1),
    KUNGFU(2),
    ;
    private final int v;

    RightClickType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static RightClickType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
