package org.y1000.entities.players;

import org.y1000.item.Equipment;
import org.y1000.message.PlayerChangeStateEvent;

class PlayerHelloState extends AbstractPlayerState {

    public PlayerHelloState(PlayerImpl player) {
        super(player, PlayerStateEnum.Hello, 750);
    }
    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            player().changeState(PlayerStandState.idle(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }
}
