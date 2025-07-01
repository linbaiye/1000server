package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateMessage;

@Slf4j
final class PlayerSitDownState extends AbstractPlayerState implements PlayerEquipableState {

    public PlayerSitDownState(PlayerImpl player) {
        super(player, PlayerStateEnum.Sit, 750);
    }

    public static PlayerSitDownState sit(PlayerImpl player) {
        return new PlayerSitDownState(player);
    }

    @Override
    public void update(int delta) {
        if (elapsedMillis() >= totalMillis()) {
            return;
        }
        elapse(delta);
    }

    private void stand() {
        player().changeState(new PlayerStandUpState(player()));
        player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
    }

    @Override
    public void sitOrStandUp() {
        if (elapsedMillis() >= totalMillis()) {
            stand();
        }
    }

    @Override
    public void doubleClickFootKungFu(FootKungFu footKungFu) {
        if (elapsedMillis() >= totalMillis()) {
            player().toggleFootKungFu(footKungFu);
            stand();
        }
    }

    @Override
    public void doubleClickBreathKungFu(BreathKungFu breathKungFu) {
        if (elapsedMillis() >= totalMillis()) {
            player().toggleBreathKungFu(breathKungFu);
        }
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }
}
