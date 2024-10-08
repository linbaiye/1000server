package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerUpdateGuildEvent;
import org.y1000.message.PlayerTextEvent;

public final class DismissGuildEvent implements BroadcastEvent {
    private final int guildId;
    public DismissGuildEvent(int guildId) {
        this.guildId = guildId;
    }
    @Override
    public void send(Player player) {
        player.guildMembership().ifPresent(membership -> {
            if (membership.guildId() == guildId) {
                player.quitGuild();
                player.emitEvent(PlayerTextEvent.systemTip(player, "门派已被解散。"));
                player.emitEvent(new PlayerUpdateGuildEvent(player));
            }
        });
    }
}
