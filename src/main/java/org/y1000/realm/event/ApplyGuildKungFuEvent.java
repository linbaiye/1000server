package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class ApplyGuildKungFuEvent implements RealmEvent {

    private final Player player;

    public ApplyGuildKungFuEvent(Player player) {
        this.player = player;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.applyGuildKungFu(player);
    }
}
