package org.y1000.entities.creatures.players;

import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.StopMoveMessage;

import java.util.Optional;

interface PlayerState {

    Optional<Message> sit(PlayerImpl player);

    Optional<Message> move(PlayerImpl player, MoveMessage moveMessage);

    default Optional<Message> stopMove(PlayerImpl player, StopMoveMessage stopMoveMessage) {
        return Optional.empty();
    }

    Optional<Message> update(PlayerImpl player, long deltaMillis);
}
