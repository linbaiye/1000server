package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class GrantGuildKungFuEvent implements RealmEvent {
    private final Player player;

    private final String toPlayer;

    public GrantGuildKungFuEvent(Player player, String toPlayer) {
        this.player = player;
        this.toPlayer = toPlayer;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.grantGuildKungFu(player, toPlayer);
    }
}
