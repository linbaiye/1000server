package org.y1000.message;

import org.y1000.entities.players.Player;

public interface PlayerMessage extends I2ClientMessage {
    Player source();

}
