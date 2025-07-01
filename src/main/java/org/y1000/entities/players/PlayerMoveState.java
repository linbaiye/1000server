package org.y1000.entities.players;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.message.PlayerChangeStateMessage;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;

import java.util.Map;

@Slf4j
public final class PlayerMoveState extends AbstractPlayerState {

    @Getter
    private final MoveAction moveAction;

    private final MoveInput currentInput;

    private MoveInput newInput;

    private static final Map<MoveAction, Integer> MoveStateMillis = Map.of(
            MoveAction.Walk, 840,
            MoveAction.Run, 420,
            MoveAction.Fly, 360,
            MoveAction.FightWalk, 840
    );

    private PlayerMoveState(PlayerImpl player,
            MoveInput input,
            MoveAction moveAction) {
        super(player, PlayerStateEnum.Move, MoveStateMillis.get(moveAction));
        this.moveAction = moveAction;
        currentInput = input;
    }

    private void changeToStand() {
        if (moveAction == MoveAction.FightWalk) {
            player().changeState(PlayerStandState.fightStand(player()));
        } else {
            player().changeState(PlayerStandState.idle(player()));
        }
        player().sendMessage(PlayerChangeStateMessage.noSelf(player()));
    }

    @Override
    public void update(int delta) {
        if (elapsedMillis() == 0) {
            if (!player().coordinate().equals(currentInput.from()) || !player().realmMap().movable(currentInput.destination())) {
                log.debug("Reset position current {}, input {}, target moveable? {}.", player().coordinate(), currentInput.from(),
                        player().realmMap().movable(currentInput.destination()));
                player().emitEvent(SetPositionEvent.of(player()));
                changeToStand();
                return;
            }
            player().changeDirection(currentInput.direction());
            player().emitEvent(PlayerMoveEvent.movingBy(player(), currentInput.direction(), moveAction));
        }
        if (!elapse(delta))
            return;
        if (!player().realmMap().movable(currentInput.destination())) {
            player().emitEvent(SetPositionEvent.of(player()));
            changeToStand();
            return;
        }
        player().changeCoordinate(currentInput.destination());
        log.debug("Player {} moved to {}.", player(), player().coordinate());
        if (newInput != null) {
            player().changeState(new PlayerMoveState(player(), newInput, computeNonFightMoveAction(player(), moveAction)));
        } else {
            changeToStand();
        }
    }


    @Override
    public void move(MoveInput moveInput) {
        newInput = moveInput;
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    private static MoveAction computeNonFightMoveAction(PlayerImpl player, MoveAction current) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(current);
    }

    private static MoveAction computeNonFightMoveAction(PlayerImpl player) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(MoveAction.Walk);
    }

    public static PlayerMoveState noneFightMove(PlayerImpl player, MoveInput input) {
        return new PlayerMoveState(player, input, computeNonFightMoveAction(player));
    }

    public static PlayerMoveState fightWalk(PlayerImpl player, MoveInput moveInput) {
        return new PlayerMoveState(player, moveInput, MoveAction.FightWalk);
    }

    @Override
    public void doubleClickAttackKungFu(AttackKungFu attackKungFu) {
        player().tryUseAttackKungFu(attackKungFu);
    }
}
