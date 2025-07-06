package org.y1000.entities.players;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.event.PlayerMovedEvent;
import org.y1000.entities.players.event.PlayerSetPositionEvent;
import org.y1000.item.Equipment;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.entities.players.event.PlayerMoveEvent;
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

    private PlayerMoveState(PlayerInternal player,
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
    }

    public MoveAction moveAction() {
        return moveAction;
    }

    @Override
    public void update(int delta) {
        if (!elapse(delta))
            return;
        player().footKungFu().ifPresent(footKungFu -> footKungFu.tryGainExpAndUseResources(player()));
        if (!player().realmMap().movable(currentInput.destination())) {
            changeToStand();
            player().sendEvent(PlayerSetPositionEvent.of(player()));
            return;
        }
        player().changeCoordinate(currentInput.destination());
        player().sendEvent(new PlayerMovedEvent(player()));
        log.debug("Player {} moved to {}.", player(), player().coordinate());
        if (newInput == null) {
            changeToStand();
            return;
        }
        if (!player().coordinate().equals(newInput.from())) {
            changeToStand();
            player().sendEvent(PlayerSetPositionEvent.of(player()));
            return;
        }
        player().changeDirection(newInput.direction());
        if (!player().realmMap().movable(newInput.destination())) {
            changeToStand();
            player().sendEvent(PlayerSetPositionEvent.of(player()));
        } else {
            MoveAction newAction = computeMoveAction(player(), moveAction);
            player().changeState(new PlayerMoveState(player(), newInput, newAction));
            player().sendEvent(PlayerMoveEvent.moveBy(player(), moveAction));
        }
    }


    @Override
    public void tryMove(MoveInput moveInput) {
        newInput = moveInput;
    }

    @Override
    public void equip(int slot, Equipment equipment) {
        player().tryEquipFromSlot(slot, equipment);
    }

    private static MoveAction computeMoveAction(PlayerInternal player, MoveAction current) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(current);
    }

    private static MoveAction computeNonFightMoveAction(PlayerInternal player) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(MoveAction.Walk);
    }

    static PlayerMoveState noneFightMove(PlayerInternal player, MoveInput input) {
        return new PlayerMoveState(player, input, computeNonFightMoveAction(player));
    }

    static PlayerMoveState fightWalk(PlayerInternal player, MoveInput moveInput) {
        return new PlayerMoveState(player, moveInput, MoveAction.FightWalk);
    }

    @Override
    public void tryToggleAttackKungFu(AttackKungFu attackKungFu) {
        player().tryUseAttackKungFu(attackKungFu);
    }
}
