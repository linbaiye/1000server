package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;

public interface ClientEventHandler {

    void handle(PlayerImpl player, ClientAttackEvent event);

    void handle(PlayerImpl player, CharacterMovementEvent event);
}
