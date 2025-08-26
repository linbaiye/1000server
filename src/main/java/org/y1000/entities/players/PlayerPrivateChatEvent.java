package org.y1000.entities.players;

import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.realm.event.DeliveryPrivateChatEvent;

public class PlayerPrivateChatEvent implements PlayerEvent  {
    private final Player player;
    private final String toPlayerName;
    private final String content;

    public PlayerPrivateChatEvent(Player player, String toPlayerName, String content) {
        this.player = player;
        this.toPlayerName = toPlayerName;
        this.content = content;
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendCrossRealmEvent(new DeliveryPrivateChatEvent(player.id(), content, toPlayerName, player.viewName()));
    }

    @Override
    public Player source() {
        return player;
    }

    public static PlayerPrivateChatEvent of(Player player, String toPlayerName, String content) {
        return new PlayerPrivateChatEvent(player, toPlayerName, content);
    }
}
