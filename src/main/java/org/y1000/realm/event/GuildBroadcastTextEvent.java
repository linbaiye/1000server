package org.y1000.realm.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.TextMessage;

public final class GuildBroadcastTextEvent extends AbstractBroadcastTextEvent {
    private final int guildId;
    private GuildBroadcastTextEvent(String text,
                                    TextMessage.TextType textType, TextMessage.ColorType colorType, TextMessage.Location location, int guildId) {
        super(text, textType, colorType, location);
        this.guildId = guildId;
    }

    @Override
    public void send(Player player) {
        if (player != null)
            player.guildMembership().filter(guildMembership -> guildMembership.guildId() == guildId)
                    .ifPresent(m -> player.emitEvent(PlayerTextEvent.systemTip(player, text)));
    }

    public static GuildBroadcastTextEvent tip(int guildId, String text) {
        Validate.notNull(text);
        return new GuildBroadcastTextEvent(text, TextMessage.TextType.CUSTOM, TextMessage.ColorType.SYSTEM_TIP, TextMessage.Location.DOWN, guildId);
    }
}
