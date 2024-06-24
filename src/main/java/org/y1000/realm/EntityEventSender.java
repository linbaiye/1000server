package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.network.Connection;

public interface EntityEventSender {
    void add(Player player, Connection connection);

    boolean contains(Player player);

    void remove(Player player);

    /**
     * Add an entity so that events happened to this entity can be sent to visible players & entities.
     * @param entity entity to add,
     */
    void add(PhysicalEntity entity);

    void remove(PhysicalEntity entity);

    void sendEvent(EntityEvent entityEvent);
}
