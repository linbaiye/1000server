package org.y1000.entities.players;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;

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

    private PlayerMoveState(Player player,
            MoveInput input,
            MoveAction moveAction) {
        super(player, OldPlayerStateEnum.Move, MoveStateMillis.get(moveAction));
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

    @Override
    public void update(int delta) {
        if (elapsedMillis() == 0) {
            if (!player().coordinate().equals(currentInput.from()) || !player().realmMap().movable(currentInput.target())) {
                log.debug("Reset position current {}, input {}, target moveable? {}.", player().coordinate(), currentInput.from(),
                        player().realmMap().movable(currentInput.target()));
                player().emitEvent(SetPositionEvent.of(player()));
                changeToStand();
                return;
            }
            player().changeDirection(currentInput.direction());
            player().emitEvent(PlayerMoveEvent.movingBy(player(), currentInput.direction(), moveAction));
        }
        if (!elapse(delta))
            return;
        if (!player().realmMap().movable(currentInput.target())) {
            player().emitEvent(SetPositionEvent.of(player()));
            changeToStand();
            return;
        }
        player().changeCoordinate(currentInput.target());
        log.debug("Player {} moved to {}.", player(), player().coordinate());
        if (newInput != null) {
            player().changeState(new PlayerMoveState(player(), newInput, computeMoveAction(player(), moveAction)));
        } else {
            changeToStand();
        }
    }


    @Override
    public void move(MoveInput moveInput) {
        newInput = moveInput;
        log.debug("Received input.");
    }

    @Override
    public void turn(TurnInput turnInput) {

    }

    private static MoveAction computeMoveAction(Player player, MoveAction current) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(current);
    }

    private static MoveAction computeMoveAction(Player player) {
        return player.footKungFu().map(k -> k.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(MoveAction.Walk);
    }

    public static PlayerMoveState noneFightWalk(Player player, MoveInput input) {
        return new PlayerMoveState(player, input, computeMoveAction(player));
    }

    public static PlayerMoveState fightWalk(Player player, MoveInput moveInput) {
        return new PlayerMoveState(player, moveInput, MoveAction.FightWalk);
    }
}
