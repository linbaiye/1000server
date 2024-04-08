package org.y1000.entities.players;

import org.y1000.message.ServerEvent;
import org.y1000.message.clientevent.CharacterMovementEvent;

import java.util.List;

interface PlayerState {

    List<ServerEvent> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event);

    State getState();

    long elapsedMillis();

    List<ServerEvent> update(PlayerImpl player, long deltaMillis);
}

