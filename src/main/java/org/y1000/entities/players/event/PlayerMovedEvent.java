package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.realm.PlayerEventHandler;

public class PlayerMovedEvent implements PlayerEvent {
    private final Player player;

    public PlayerMovedEvent(Player player) {
        this.player = player;
    }
    @Override
    public void accept(PlayerEventHandler handler) {
        handler.onMoved(source());
    }

    @Override
    public Player source() {
        return player;
    }
}
