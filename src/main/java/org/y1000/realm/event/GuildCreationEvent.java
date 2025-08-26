package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public record GuildCreationEvent(Player player, boolean confirm, int slot, String name) implements RealmEvent {
    @Override
    public void accept(RealmEventHandler handler) {
        if (confirm)
            handler.confirmGuildCreation(player, slot, name);
        else
            handler.cancelGuildCreation(player);
    }

    public static GuildCreationEvent confirm(Player player, int slot, String name) {
        return new GuildCreationEvent(player, true, slot, name);
    }

    public static GuildCreationEvent cancel(Player player) {
        return new GuildCreationEvent(player, false, 0, null);
    }
}
