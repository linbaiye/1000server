package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.IActiveEntity;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.players.Damage;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Optional;

public interface INpc extends Creature {

    void onActionDone();

    void onMoveFailed();

    void move(int millis);

    void stay(int millis);

    void turn();

    void die();

    Rectangle wanderingArea();

    Coordinate spawnCoordinate();

    <S extends NpcSpell> Optional<S> findSpell(Class<S> type);

    void startAction(NpcAction stateEnum);

    void changeState(NpcState state);

    NpcState npcState();

    void start();

    /**
     * An viewName is used identify a npc uniquely as different NPCs can have the same viewName.
     * @return the unique viewName.
     */
    String idName();

    /**
     * Gets attacked by aoe skills.
     * @param caster the attacker.
     * @param hit attacker's hit.
     * @param damage attacker's damage.
     * @return exp the attacker can get.
     */
    int attackedByAoe(IActiveEntity caster, int hit, Damage damage);

    int walkSpeed();

    String animation();

    String shape();

    NpcAction npcStateEnum();

    default <A> Optional<A> findAbility(Class<A> type) {
        return Optional.empty();
    }

    int getStateMillis(NpcAction stateEnum);

    default boolean isDead() {
        return npcStateEnum() == NpcAction.Die;
    }

    default boolean isMoving() {
        return npcStateEnum() == NpcAction.Move;
    }
}
