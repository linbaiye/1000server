package org.y1000.entities;

import org.y1000.entities.creatures.npc.NpcHurtAbility;
import org.y1000.event.TypedEntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


public interface ActiveEntity extends Entity {

    void update(int delta);

    void emitEvent(TypedEntityEvent event);

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);

    void clearListeners();

    <AB> Optional<AB> findAbility(Class<AB> type);

    default Set<Entity> getEntitiesAt(Set<Coordinate> coordinates) {
        return Collections.emptySet();
    }

    default Optional<String> clickText() {
        return Optional.empty();
    }

    default boolean isDead() {
        return findAbility(HurtAbility.class)
                .map(hurtAbility -> hurtAbility.currentLife() <= 0)
                .orElse(false);
    }

}
