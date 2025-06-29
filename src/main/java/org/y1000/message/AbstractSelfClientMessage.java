package org.y1000.message;

import lombok.Getter;
import org.y1000.entities.players.Player;

public abstract class AbstractSelfClientMessage implements I2ClientMessage {

    @Getter
    private final Player player;

    protected AbstractSelfClientMessage(Player player) {
        this.player = player;
    }

}
