package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.input.ApplyGuildKungFuInput;

public final class ApplyKungFuEvent implements RealmEvent {
    private final Player player;
    private final ApplyGuildKungFuInput params;

    public ApplyKungFuEvent(Player player, ApplyGuildKungFuInput params) {
        this.player = player;
        this.params = params;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.applyGuildKungFu(player, params);
    }
}
