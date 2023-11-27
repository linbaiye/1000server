package org.y1000.message;

public enum MessageType {

    MOVE(0),

    TURN(1),

    POSITION(2),

    STOP_MOVE(3),

    CONFIRM(4),

    ;

    private final int v;
    MessageType(int v) {
        this.v = v;
    }

    public int value() {
        return v;
    }

    public static MessageType fromValue(int v) {
        for (MessageType value : values()) {
            if (value.v == v) {
                return value;
            }
        }
        return null;
    }
}
