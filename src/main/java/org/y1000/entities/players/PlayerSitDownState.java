package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.equipment.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;

@Slf4j
final class PlayerSitDownState extends AbstractPlayerState {

    static final int StateMillis = 750;
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

    private boolean pastHalfTime() {
        return elapsedMillis() >=  totalMillis() / 2;
    }

    @Override
    public void handleAfterHurt() {
        reset();
        player().changeState(this);
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    private void standUp() {
        player().changeState(new PlayerStandUpState(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    private void standIdle() {
        player().changeState(PlayerStandState.idle(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void sitOrStandUp() {
        standUpOrStandIdle();
    }

    private void standUpOrStandIdle() {
        if (pastHalfTime()) {
            standUp();
        } else {
            standIdle();
        }
        player().disableBreathAndSync();
    }

    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        player().toggleFootAndSync(footKungFu);
        standUpOrStandIdle();
    }

    @Override
    public void tryToggleBreathKungFu(BreathKungFu breathKungFu) {
        player().toggleBreathAndSync(breathKungFu);
    }

    @Override
    public void attack(ActiveEntity target) {
        if (player().tryAcceptAttack(target) != 0)
            return;
        if (pastHalfTime()) {
            player().changeState(PlayerStandUpState.toCombat(player()));
        } else {
            player().changeState(PlayerStandState.fightStand(player()));
        }
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
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
