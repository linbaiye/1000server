package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateMessage;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

final class PlayerStandState extends AbstractPlayerState {

    private PlayerStandState(PlayerImpl player, PlayerStateEnum stateEnum) {
        super(player, stateEnum, 1800);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            reset();
        }
    }

    public void move(MoveInput moveInput) {
        if (playerStateEnum() == PlayerStateEnum.Idle) {
            player().changeState(PlayerMoveState.noneFightMove(player(), moveInput));
        } else {
            player().changeState(PlayerMoveState.fightWalk(player(), moveInput));
        }
    }

    public void turn(TurnInput turnInput) {
        player().changeDirection(turnInput.direction());
        player().emitEvent(SetPositionEvent.of(player()));
    }

    @Override
    public void sitOrStandUp() {
        if (playerStateEnum() == PlayerStateEnum.Idle ||
                PlayerStateEnum.FightStand == playerStateEnum()) {
            player().disableFootKungFuAndSync();
            player().changeState(PlayerSitDownState.sit(player()));
            player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
        }
    }

    @Override
    public void switchStand() {
        player().disableFootKungFuAndSync();
        if (playerStateEnum() == PlayerStateEnum.Idle) {
            player().changeState(PlayerStandState.fightStand(player()));
        } else if (playerStateEnum() == PlayerStateEnum.FightStand) {
            player().changeState(PlayerStandState.idle(player()));
        } else {
            return;
        }
        player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
    }


    @Override
    public void doubleClickFootKungFu(FootKungFu footKungFu) {
        if (playerStateEnum() == PlayerStateEnum.FightStand) {
            switchStand();
        }
        player().toggleFootKungFu(footKungFu);
    }

    @Override
    public void sayHello() {
        if (playerStateEnum() == PlayerStateEnum.Idle) {
            player().changeState(new PlayerHelloState(player()));
            player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
        }
    }

    @Override
    public void doubleClickBreathKungFu(BreathKungFu breathKungFu) {
        player().toggleBreathKungFu(breathKungFu);
        player().changeState(PlayerSitDownState.sit(player()));
        player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
    }

    public static PlayerStandState idle(PlayerImpl player) {
        Validate.notNull(player);
        return new PlayerStandState(player, PlayerStateEnum.Idle);
    }

    public static PlayerStandState fightStand(PlayerImpl player) {
        Validate.notNull(player);
        return new PlayerStandState(player, PlayerStateEnum.FightStand);
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
