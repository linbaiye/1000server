package org.y1000.message.input;

import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerIdleState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.SetPositionEvent;

public interface MoveInput extends InputMessage {

    void move(PlayerImpl player);

    default void changeToIdle(PlayerImpl player) {
        player.changeState(new PlayerIdleState(player.getStateMillis(State.IDLE)));
        player.emitEvent(new InputResponseMessage(sequence(), SetPositionEvent.fromCreature(player)));
    }
}
