package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;

public interface PlayerState extends CreatureState<PlayerImpl> {

    default void attackKungFuTypeChanged(PlayerImpl player) {

    }

    default void afterHurt(PlayerImpl player) {

    }

    default boolean canUseFootKungFu() {
        return true;
    }

    default boolean canAttack() {
        return true;
    }

    default boolean canSitDown() {
        return false;
    }

    default boolean canStandUp() {
        return false;
    }

    @Override
    default State decideAfterHurtState(PlayerImpl player) {
        return player.isFighting() ? State.COOLDOWN : State.IDLE;
    }
}
