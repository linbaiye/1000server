package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.managers.AbstractEntityManager;
import org.y1000.entities.PlayerVisibleScope;
import org.y1000.message.serverevent.*;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class CreatureManager extends AbstractEntityManager<Creature> implements
        EntityEventListener, EntityEventHandler {
    private final Set<PassiveMonster> passiveMonsters;
    private final EntityEventListener eventListener;
    private final RealmMap realmMap;

    public CreatureManager(EntityEventListener eventListener, RealmMap realmMap) {
        this.eventListener = eventListener;
        passiveMonsters = new HashSet<>();
        this.realmMap = realmMap;
        initializeCreatures();
    }



    @Override
    public void update(long delta) {
        passiveMonsters.forEach(m -> m.update(delta));
    }


    public Set<Creature> visibleCreatures(PlayerVisibleScope scope) {
        return passiveMonsters.stream().filter(scope::withinScope)
                .collect(Collectors.toSet());
    }

    @Override
    public void OnEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }

    private void initializeCreatures() {
        var monster = new PassiveMonster(3L, new Coordinate(39, 30), Direction.DOWN, "牛", realmMap);
        monster.registerEventListener(eventListener);
        monster.registerEventListener(this);
        passiveMonsters.add(monster);
    }

}
