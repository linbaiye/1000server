package org.y1000.message.clientevent;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Player;

public interface ClientSingleInteractEvent extends ClientEvent {

    long targetId();

    void handle(Player player, ActiveEntity entity);
}
