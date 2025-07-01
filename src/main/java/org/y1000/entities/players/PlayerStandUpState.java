package org.y1000.entities.players;


import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.message.PlayerChangeStateMessage;

final class PlayerStandUpState extends AbstractPlayerState {

    public PlayerStandUpState(PlayerImpl player) {
        super(player, PlayerStateEnum.StandUp, 750);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            player().changeState(PlayerStandState.idle(player()));
            player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
        }
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    @Override
    public void doubleClickAttackKungFu(AttackKungFu attackKungFu) {
        player().tryUseAttackKungFu(attackKungFu);
    }
}
