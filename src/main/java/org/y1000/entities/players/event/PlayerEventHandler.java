package org.y1000.entities.players.event;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;

import java.util.function.Predicate;

public interface PlayerEventHandler {

    void sendToVisiblePlayers(Entity entity, I2ClientMessage message);

    void sendTo(Player player, I2ClientMessage message);

    /**
     * Apply filter to select from all players to send the message to.
     * @param filter
     * @param message
     */
    void sendToPlayers(Predicate<? super Player> filter, I2ClientMessage message);
}
