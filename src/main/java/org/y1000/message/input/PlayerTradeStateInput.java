package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.message.ValueEnum;

public record PlayerTradeStateInput(int v) implements SelfHandleInput {

    public enum State implements ValueEnum {
        Cancel(1),
        Confirm(2),
        Unconfirmed(3),
        ;

        private final int v;

        State(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }

        public static State fromValue(int v) {
            return ValueEnum.fromValueOrThrow(values(), v);
        }
    }
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.changeTradeState(State.fromValue(v));
    }
}
