package org.y1000.entities.creatures.monster;

import lombok.Builder;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;

public class AggressiveMonster extends AbstractMonster{
    @Builder
    public AggressiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap, int avoidance, int recovery, int attackSpeed,
                          int life, int wanderingRange, int armor, Map<State, Integer> stateMillis, String attackSound) {
        super(id, coordinate, direction, name, realmMap, avoidance, recovery, attackSpeed, life, wanderingRange, armor, stateMillis, attackSound);
    }

    @Override
    protected Logger log() {
        return null;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }
}
