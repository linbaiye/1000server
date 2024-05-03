package org.y1000.message.input;

import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.InputPacket;

public record RightMouseRelease(long sequence) implements MoveInput {

    @Override
    public InputType type() {
        return InputType.MOUSE_RIGHT_RELEASE;
    }

    public static RightMouseRelease fromPacket(InputPacket inputPacket) {
        return new RightMouseRelease(inputPacket.getSequence());
    }

    @Override
    public void move(PlayerImpl player) {
        changeToIdle(player);
    }
}
