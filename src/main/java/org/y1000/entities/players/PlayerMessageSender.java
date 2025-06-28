package org.y1000.entities.players;

import org.y1000.message.I2ClientMessage;

public interface PlayerMessageSender {
    void send(I2ClientMessage message);
}
