package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;

public interface BiClientEventVisitor {

    default void visit(PlayerImpl player, ClientAttackEvent event) {}

    default void visit(PlayerImpl player, ClientMovementEvent event) {}

}
