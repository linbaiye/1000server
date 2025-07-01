package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

public interface PlayerState extends CreatureState {

    PlayerStateEnum playerStateEnum();

    default void move(MoveInput input) {

    }

    default void sayHello() {

    }

    default void sitOrStandUp() {

    }

    default void switchStand() {

    }

    default void doubleClickFootKungFu(FootKungFu footKungFu) {

    }

    default void doubleClickBreathKungFu(BreathKungFu breathKungFu) {

    }

    default void doubleClickAttackKungFu(AttackKungFu attackKungFu) {
        
    }


    default void turn(TurnInput input) {

    }

    default void equip(int slot, Equipment equipment) {

    }

}
