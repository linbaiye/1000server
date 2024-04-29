package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;

public interface ClientEventVisitor {

    void visit(PlayerImpl player, ClientAttackEvent event);

    void visit(PlayerImpl player, CharacterMovementEvent event);
}
