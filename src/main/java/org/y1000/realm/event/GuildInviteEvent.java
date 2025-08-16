package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public class GuildInviteEvent implements RealmEvent {
    private final Player founder;
    private final String inviteeName;

    public GuildInviteEvent(Player founder, String inviteeName) {
        this.founder = founder;
        this.inviteeName = inviteeName;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.guildInvite(founder, inviteeName);
    }
}
