package org.y1000.message.clientevent;

import org.y1000.message.ValueEnum;

public record ClientSimpleCommandEvent(org.y1000.message.clientevent.ClientSimpleCommandEvent.Command command) implements ClientEvent {

    public enum Command implements ValueEnum {
        NPC_POSITION(1);
        private final int v;

        Command(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }

        public static Command fromValue(int v) {
            return ValueEnum.fromValueOrThrow(Command.values(), v);
        }
    }

    public static ClientSimpleCommandEvent parse(int v) {
        return new ClientSimpleCommandEvent(Command.fromValue(v));
    }

    public boolean isAskingPosition() {
        return command == Command.NPC_POSITION;
    }
}
