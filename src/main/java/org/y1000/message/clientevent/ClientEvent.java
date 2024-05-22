package org.y1000.message.clientevent;


import org.y1000.entities.players.PlayerImpl;

public interface ClientEvent {

    default void accept(PlayerImpl player, BiClientEventVisitor handler) {

    }

}
