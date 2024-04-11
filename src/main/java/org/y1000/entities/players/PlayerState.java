package org.y1000.entities.players;

import org.y1000.message.ServerEvent;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;

import java.util.List;

interface PlayerState {

    State getState();

    long elapsedMillis();

    void update(PlayerImpl player, long deltaMillis);
}

