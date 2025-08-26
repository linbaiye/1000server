package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;

public interface MessageSender {

    void sendTo(Player player, I2ClientMessage message);

}
