package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class KickGuildMember implements RealmEvent {
    private final Player player;
    private final String kickee;

    public KickGuildMember(Player player, String kickee) {
        this.player = player;
        this.kickee = kickee;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.kickGuildMember(player, kickee);
    }
}
