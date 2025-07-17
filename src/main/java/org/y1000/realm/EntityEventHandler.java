package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.message.I2ClientMessage;

import java.util.Set;
import java.util.function.Predicate;

public interface EntityEventHandler {

    void sendToVisiblePlayers(Entity source, I2ClientMessage message);

    // Should an ActiveEntityHandler be abstracted?
    Set<Entity> filterVisible(Entity source, Predicate<Entity> filter);
}
