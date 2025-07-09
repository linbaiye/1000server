package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.item.Equipment;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateEvent;
import org.y1000.entities.players.event.PlayerMoveEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

@Slf4j
final class PlayerStandState extends AbstractPlayerState {

    private PlayerStandState(PlayerImpl player, PlayerStateEnum stateEnum) {
        super(player, stateEnum, 1800);
    }

    @Override
    public void update(int delta) {
        if (player().updateCombat(delta)) {
            return;
        }
        if (elapse(delta)) {
            reset();
        }
    }

    public void tryMove(MoveInput moveInput) {
        if (!player().coordinate().equals(moveInput.from())) {
            log.debug("Current {}, input {}, rewind {}.", player().coordinate(), moveInput.from(), player().id());
            player().sendEvent(PlayerSetPositionAndStateEvent.of(player()));
            reset();
            return;
        }
        player().changeDirection(moveInput.direction());
        if (!player().movable(moveInput.destination())) {
            log.debug("Destination occupied {}, set position of {}.", player().coordinate(), player().id());
            player().sendEvent(PlayerSetPositionAndStateEvent.of(player()));
            reset();
            return;
        }
        PlayerMoveState moveState = playerStateEnum() == PlayerStateEnum.Idle ?
                PlayerMoveState.noneFightMove(player(), moveInput) :
                PlayerMoveState.fightWalk(player(), moveInput);
        player().changeState(moveState);
        player().sendEvent(PlayerMoveEvent.moveBy(player(), moveState.moveAction()));
    }

    public void turn(TurnInput turnInput) {
        reset();
        player().changeDirection(turnInput.direction());
        player().sendEvent(PlayerChangeStateEvent.noSelf(player()));
    }

    @Override
    public void sitOrStandUp() {
        player().disableFootKungFuAndSync();
        player().stopFight();
        player().changeState(PlayerSitDownState.sit(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void switchStand() {
        player().disableFootKungFuAndSync();
        if (playerStateEnum() == PlayerStateEnum.Idle) {
            player().changeState(PlayerStandState.fightStand(player()));
        } else {
            player().stopFight();
            player().changeState(PlayerStandState.idle(player()));
        }
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }


    @Override
    public void tryToggleFootKungFu(FootKungFu footKungFu) {
        if (playerStateEnum() == PlayerStateEnum.FightStand) {
            player().changeState(PlayerStandState.idle(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
        player().toggleFootKungFu(footKungFu);
    }

    @Override
    public void sayHello() {
        if (playerStateEnum() == PlayerStateEnum.Idle) {
            player().changeState(new PlayerHelloState(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void handleAfterHurt() {
        player().changeState(new PlayerStandState(player(), playerStateEnum()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    @Override
    public void attack(Entity target) {
        player().acceptAttack(target);
    }

    @Override
    public void tryToggleBreathKungFu(BreathKungFu breathKungFu) {
        player().toggleBreathKungFu(breathKungFu);
        player().changeState(PlayerSitDownState.sit(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
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
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryChangeAttackKungFu(attackKungFu);
    }
}
