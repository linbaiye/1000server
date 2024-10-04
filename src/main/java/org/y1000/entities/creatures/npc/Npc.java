package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.players.Damage;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Optional;

public interface Npc extends Creature {

    void onActionDone();

    void onMoveFailed();

    void move(int millis);

    void stay(int millis);

    void die();

    Rectangle wanderingArea();

    Coordinate spawnCoordinate();

    <S extends NpcSpell> Optional<S> findSpell(Class<S> type);

    void startAction(State state);

    void changeState(NpcState state);

    NpcState state();

    void start();

    /**
     * An idName is used identify a npc uniquely as different NPCs can have the same idName.
     * @return the unique idName.
     */
    String idName();

    /**
     * Gets attacked by aoe skills.
     * @param caster the attacker.
     * @param hit attacker's hit.
     * @param damage attacker's damage.
     * @return exp the attacker can get.
     */
    int attackedByAoe(ViolentCreature caster, int hit, Damage damage);

    int walkSpeed();

    int viewWidth();


    NpcAI getAI();

    void changeAndStartAI(NpcAI newAI);

    void changeAI(NpcAI newAI);

    void changeToIdleAI();

    void startIdleAI();
}
