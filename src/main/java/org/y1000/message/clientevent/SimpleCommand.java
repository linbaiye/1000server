package org.y1000.message.clientevent;

import org.y1000.message.ValueEnum;

public enum SimpleCommand implements ValueEnum {
    NPC_POSITION(1),
    CLIENT_QUIT(2),
    ;
    private final int v;

    SimpleCommand(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static SimpleCommand fromValue(int v) {
        return ValueEnum.fromValueOrThrow(SimpleCommand.values(), v);
    }
}
