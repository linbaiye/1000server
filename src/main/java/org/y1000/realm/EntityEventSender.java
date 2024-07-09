package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.event.EntityEvent;
import org.y1000.message.ServerMessage;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.Connection;

public interface EntityEventSender {
    void add(Player player, Connection connection);

    boolean contains(Player player);

    void remove(Player player);

    /**
     * Add an entity so that events happened to this entity can be sent to visible players & entities.
     * @param entity entity to add,
     */
    void add(Entity entity);

    void remove(Entity entity);

    void sendEvent(EntityEvent entityEvent);

    void notifyVisiblePlayers(Entity source, ServerMessage serverMessage);

    default void notifySelf(AbstractPlayerEvent playerEvent) {

    }

}
