package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateEvent;

@Slf4j
final class PlayerSitDownState extends AbstractPlayerState implements PlayerEquipableState {

    private static final int StateMillis = 750;
    public PlayerSitDownState(PlayerImpl player) {
        super(player, PlayerStateEnum.Sit, StateMillis);
    }

    public static PlayerSitDownState sit(PlayerImpl player) {
        return new PlayerSitDownState(player);
    }

    @Override
    public void update(int delta) {
        elapse(delta);
    }

    @Override
    public void handleAfterHurt() {
        reset();
        player().changeState(this);
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    private void stand() {
        player().changeState(new PlayerStandUpState(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void sitOrStandUp() {
        if (elapsedMillis() >= totalMillis()) {
            stand();
        }
    }

    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        if (elapsedMillis() >= totalMillis()) {
            player().toggleFootKungFu(footKungFu);
            stand();
        }
    }

    @Override
    public void tryToggleBreathKungFu(BreathKungFu breathKungFu) {
        if (elapsedMillis() >= totalMillis()) {
            player().toggleBreathKungFu(breathKungFu);
        }
    }

    @Override
    public void attack(Entity target) {
        if (elapsedMillis() >= totalMillis()) {
            player().acceptAttack(target);
        }
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryChangeAttackKungFu(attackKungFu);
    }
}
