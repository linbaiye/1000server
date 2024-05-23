package org.y1000.entities.players.kungfu.attack;


import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.LevelKungFu;

public interface AttackKungFu extends LevelKungFu {

    int getBodyDamage();

    int getBodyArmor();

    int getAttackSpeed();

    int getRecovery();

    State randomAttackState();

    boolean hasState(State state);

    AttackKungFuType getType();

    default boolean isRanged() {
        return false;
    }
}
