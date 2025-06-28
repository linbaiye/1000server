package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.message.ValueEnum;

public record SimpleInput(SimpleInput.Type type) implements SelfHandleInput {

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.handleSimpleInput(type());
    }

    public enum Type implements ValueEnum {
        KungFuBook(1),
        Inventory(2),
        ;
        private final int v;

        Type(int v) {
            this.v = v;
        }
        @Override
        public int value() {
            return v;
        }
    }

    public static SimpleInput fromValue(int v) {
        for (Type t : Type.values()) {
            if (t.v == v) {
                return new SimpleInput(t);
            }
        }
        throw new IllegalArgumentException();
    }

}
