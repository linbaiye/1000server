package org.y1000.message.clientevent;

import org.y1000.entities.players.Player;

public final class CancelBuffEvent extends AbstractClientEvent implements ClientSelfInteractEvent {

    @Override
    public void handle(Player player) {
        if (player != null)
            player.cancelBuff();
    }
}
