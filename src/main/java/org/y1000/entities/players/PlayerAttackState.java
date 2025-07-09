package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.message.PlayerChangeStateEvent;

class PlayerAttackState extends AbstractPlayerState {

    public PlayerAttackState(PlayerImpl player, AttackAction action) {
        super(player, PlayerStateEnum.Attack, action.getMillis());
    }

    @Override
    public void update(int delta) {
        if (player().updateCombat(delta)) {
            return;
        }
        if (elapse(delta)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void attack(Entity target) {
        player().acceptAttack(target);
    }

    @Override
    public void handleAfterHurt() {
        player().changeState(PlayerStandState.fightStand(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        var attack = player().attackKungFu();
        player().tryEquipFromSlot(slot, equipment);
        if (!attack.nameEquals(player().attackKungFu())) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        if (player().tryChangeAttackKungFu(attackKungFu)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }
}
