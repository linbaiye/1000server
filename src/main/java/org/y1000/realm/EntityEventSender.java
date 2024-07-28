package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.event.EntityEvent;
import org.y1000.message.ServerMessage;

public interface EntityEventSender {
    /**
     * Add an entity so that events happened to this entity can be sent to visible players & entities.
     * @param entity entity to add,
     */
    void add(Entity entity);

    void remove(Entity entity);

    void sendEvent(EntityEvent entityEvent);

    void notifyVisiblePlayers(Entity source, ServerMessage serverMessage);

    void notifyVisiblePlayersAndSelf(Entity source, ServerMessage serverMessage);

    default void notifySelf(AbstractPlayerEvent playerEvent) {

    }

    /**
     * Notify player no visible entities, also notify other players of the player.
     * @param player the player to be notified.
     */
    default void notifyPlayerOfEntities(Player player) {

    }

    default void updateScope(Player player) {

    }
}
