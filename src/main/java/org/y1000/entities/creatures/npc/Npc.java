package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface Npc extends ActiveEntity {
    void changeAI(NpcAI newAi);

    void startAI();

    default void startAI(NpcAI npcAI) {
        changeAI(npcAI);
        startAI();
    }

    Direction direction();

    void changeCoordinate(Coordinate coordinate);

    void changeDirection(Direction direction);

    void free();

    RealmMap getRealmMap();

    String getViewName();

    String getAnimate();

    String getShape();

    Coordinate getSpawnCoordinate();

    String getIdName();

    int getWanderRage();

    void sendEvent(NpcEvent event);

    <T> Optional<T> findAbility(Class<T> type, Predicate<? super T> filter);


    <T> List<T> findAbilities(Class<T> type);


    default void instantKill() {

    }

}
