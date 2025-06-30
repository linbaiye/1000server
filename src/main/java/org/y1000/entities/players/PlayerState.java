package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

public interface PlayerState extends CreatureState {
    OldPlayerStateEnum stateEnum();

    PlayerStateEnum playerStateEnum();

    void move(MoveInput input);

    default void turn(TurnInput input) {

    }

}
