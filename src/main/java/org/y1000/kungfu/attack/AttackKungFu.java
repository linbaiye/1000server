package org.y1000.kungfu.attack;


import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.kungfu.KungFu;

public interface AttackKungFu extends KungFu {

    int getBodyDamage();

    int getBodyArmor();

    int getAttackSpeed();

    int getRecovery();

    State randomAttackState();

    boolean hasState(State state);

    AttackKungFuType getType();


    default void attackAgain(PlayerImpl player) {

    }


    default boolean isRanged() {
        return false;
    }
}
