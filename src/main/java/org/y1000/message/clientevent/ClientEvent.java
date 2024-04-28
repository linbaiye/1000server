package org.y1000.message.clientevent;


import org.y1000.entities.players.PlayerImpl;

public interface ClientEvent {

    void accept(PlayerImpl player, ClientEventHandler handler);

}
