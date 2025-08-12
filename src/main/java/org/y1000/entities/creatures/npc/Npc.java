package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Npc extends Creature  {
    void changeAI(NpcAI newAi);

    void startAI();

    default void startAI(NpcAI npcAI) {
        changeAI(npcAI);
        startAI();
    }

    void free();

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
