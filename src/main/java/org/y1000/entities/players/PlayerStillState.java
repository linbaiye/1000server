package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.kungfu.FootKungFu;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.MoveInput;

import java.util.Optional;
import java.util.Set;

@Slf4j
public final class PlayerStillState extends AbstractPlayerStillState {

    private final static Set<OldPlayerStateEnum> MOVABLE_PLAYER_STATE_ENUMS = Set.of(OldPlayerStateEnum.IDLE, OldPlayerStateEnum.FightStand);

    public PlayerStillState(int millis) {
        this(millis, OldPlayerStateEnum.IDLE);
    }

    public PlayerStillState(int millis, OldPlayerStateEnum playerStateEnum) {
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
        if (stateEnum() == OldPlayerStateEnum.FightStand) {
            return IPlayerMoveState.moveBy(player, OldPlayerStateEnum.ENFIGHT_WALK, direction);
        }
        Optional<FootKungFu> footMagic = player.footKungFu();
        OldPlayerStateEnum playerStateEnum = footMagic.map(magic -> magic.canFly() ? OldPlayerStateEnum.FLY : OldPlayerStateEnum.RUN)
                .orElse(OldPlayerStateEnum.Move);
        return IPlayerMoveState.moveBy(player, playerStateEnum, direction);
    }


    public static PlayerStillState idle(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(OldPlayerStateEnum.IDLE));
    }

    public static PlayerStillState chillOut(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(OldPlayerStateEnum.FightStand), OldPlayerStateEnum.FightStand);
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
        if (stateEnum() == OldPlayerStateEnum.FightStand)
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
    public OldPlayerStateEnum decideAfterHurtState() {
        return stateEnum();
    }
}
