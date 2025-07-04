package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.IAbstractPlayerEvent;
import org.y1000.event.IEntityEvent;
import org.y1000.message.I2ClientMessage;

public interface EntityEventSender extends MessageSender {
    /**
     * Add an entity so that events happened to this entity can be sent to visible players & entities.
     * @param entity entity to add,
     */
    void add(Entity entity);

    void remove(Entity entity);

    void sendEvent(IEntityEvent entityEvent);

    void notifyVisiblePlayers(Entity source, I2ClientMessage serverMessage);

    void notifyVisiblePlayersAndSelf(Entity source, I2ClientMessage serverMessage);

    default void notifySelf(IAbstractPlayerEvent playerEvent) {

    }

    /**
     * Notify player no visible entities, also notify other players of the player.
     * @param player the player to be notified.
     */
    default void notifyPlayerOfEntities(Player player) {

    }



}
