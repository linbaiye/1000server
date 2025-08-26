package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class QuitGuildEvent implements RealmEvent {
    private final Player player;

    public QuitGuildEvent(Player player) {
        this.player = player;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.quitGuild(player);
    }
}
