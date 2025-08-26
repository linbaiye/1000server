package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class ApplyGuildKungFuCommandEvent implements RealmEvent {

    private final Player player;

    public ApplyGuildKungFuCommandEvent(Player player) {
        this.player = player;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.handleApplyKungFuCommand(player);
    }
}
