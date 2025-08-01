package org.y1000.entities.players;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

interface PlayerState extends CreatureState {

    PlayerStateEnum playerStateEnum();

    default void tryMove(MoveInput input) {

    }

    default void sayHello() {

    }

    default void sitOrStandUp() {

    }

    default void switchStand() {

    }

    default void tryToggleFootKungFu(FootKungFu footKungFu) {

    }

    default void tryToggleBreathKungFu(BreathKungFu breathKungFu) {

    }

    default void tryToggleAttackKungFu(AttackKungFu attackKungFu) {

    }


    default void turn(TurnInput input) {

    }

    default void equip(int slot, Equipment equipment) {

    }

    default void attack(ActiveEntity entity) {

    }

    default void handleAfterHurt() {

    }

    default boolean canBeDragged() {
        return false;
    }

    void changePlayer(PlayerImpl player);

}
