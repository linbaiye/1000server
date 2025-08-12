package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.network.gen.Packet;

public abstract class AbstractPlayerEvent implements PlayerEvent {
    private final Player player;

    public AbstractPlayerEvent(Player player) {
        this.player = player;
    }

    @Override
    public Player source() {
        return player;
    }
}
