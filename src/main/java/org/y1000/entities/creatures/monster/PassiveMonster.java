package org.y1000.entities.creatures.monster;


import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Objects;


@Slf4j
public final class PassiveMonster extends AbstractMonster {

    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap) {
        this(id, coordinate, direction, name, realmMap, 0, 100, 200, 0, 10);
    }

    @Builder
    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap, int avoidance, int recovery, int attackSpeed,
                          int life, int wanderingRange, int armor) {
        super(id, coordinate, direction, name, realmMap, avoidance, recovery, attackSpeed, life, wanderingRange, armor);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((PassiveMonster) obj).id() == id();
    }


    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (getFightingEntity() == null) {
            log.error("Invalid event received.");
            return;
        }
        if (!canAttack(getFightingEntity())) {
            clearFightingEntity();
        }
    }
}
