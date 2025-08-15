package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;
import org.y1000.realm.PlayerEventHandler;

public class DirectMessage implements PlayerEvent {

    private final Player player;

    private final I2ClientMessage message;

    public DirectMessage(Player player, I2ClientMessage message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendTo(player, message);
    }

    @Override
    public Player source() {
        return player;
    }

    public static DirectMessage of(Player player, I2ClientMessage message) {
        return new DirectMessage(player, message);
    }
}
