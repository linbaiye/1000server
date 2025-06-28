package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.kungfu.FootKungFu;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;

import java.util.Optional;
import java.util.Set;

@Slf4j
public final class PlayerStillState extends AbstractPlayerStillState {

    private final static Set<PlayerStateEnum> MOVABLE_PLAYER_STATE_ENUMS = Set.of(PlayerStateEnum.IDLE, PlayerStateEnum.FightStand);

    public PlayerStillState(int millis) {
        this(millis, PlayerStateEnum.IDLE);
    }

    public PlayerStillState(int millis, PlayerStateEnum playerStateEnum) {
        super(millis, playerStateEnum);
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        elapseAndHandleInput(player, deltaMillis);
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public IPlayerState moveState(PlayerImpl player, Direction direction) {
        if (stateEnum() == PlayerStateEnum.FightStand) {
            return IPlayerMoveState.moveBy(player, PlayerStateEnum.ENFIGHT_WALK, direction);
        }
        Optional<FootKungFu> footMagic = player.footKungFu();
        PlayerStateEnum playerStateEnum = footMagic.map(magic -> magic.canFly() ? PlayerStateEnum.FLY : PlayerStateEnum.RUN)
                .orElse(PlayerStateEnum.Move);
        return IPlayerMoveState.moveBy(player, playerStateEnum, direction);
    }


    public static PlayerStillState idle(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(PlayerStateEnum.IDLE));
    }

    public static PlayerStillState chillOut(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(PlayerStateEnum.FightStand), PlayerStateEnum.FightStand);
    }

    private void handleMove(PlayerImpl player, MoveInput moveInput) {
        if (player.realmMap() == null || !player.realmMap().movable(moveInput.from().moveBy(moveInput.direction()))) {
            player.emitEvent(SetPositionEvent.of(player));
            return;
        }
        if (!MOVABLE_PLAYER_STATE_ENUMS.contains(stateEnum())) {
            return;
        }
        MoveAction moveAction = computeMoveAction(player);
    }

    private MoveAction computeMoveAction(PlayerImpl player) {
        if (stateEnum() == PlayerStateEnum.FightStand)
            return MoveAction.FightWalk;
        return player.footKungFu().map(m -> m.canFly() ? MoveAction.Fly : MoveAction.Run)
                .orElse(MoveAction.Walk);
    }

    @Override
    public void handleInput(PlayerImpl player, Object input) {
        if (input instanceof MoveInput moveInput) {
            handleMove(player, moveInput);
        }
    }

    @Override
    public String toString() {
        return stateEnum().name();
    }

    @Override
    public PlayerStateEnum decideAfterHurtState() {
        return stateEnum();
    }
}
