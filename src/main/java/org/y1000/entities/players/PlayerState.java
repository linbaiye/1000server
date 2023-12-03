package org.y1000.entities.players;

import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.StopMoveMessage;
import org.y1000.message.input.RightMouseClick;

import java.util.Optional;

interface PlayerState {

    Optional<Message> sit(PlayerImpl player);

    Optional<Message> move(PlayerImpl player, MoveMessage moveMessage);

    Optional<Message> onRightMouseClicked(PlayerImpl player, RightMouseClick click);

    State getState();

    default Optional<Message> stopMove(PlayerImpl player, StopMoveMessage stopMoveMessage) {
        return Optional.empty();
    }

    Optional<Message> update(PlayerImpl player, long deltaMillis);

}

