package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;

public interface ClientEventVisitor {

    default void visit(PlayerImpl player, ClientAttackEvent event) {}

    default void visit(PlayerImpl player, ClientMovementEvent event) {}
}
