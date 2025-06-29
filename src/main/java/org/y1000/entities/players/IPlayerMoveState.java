package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.kungfu.FootKungFu;
import org.y1000.util.Coordinate;
import java.util.Set;

@Slf4j
public final class IPlayerMoveState extends AbstractPlayerMoveState {

    private static final Set<OldPlayerStateEnum> MOVE_PLAYER_STATE_ENUMS = Set.of(
            OldPlayerStateEnum.Move, OldPlayerStateEnum.RUN, OldPlayerStateEnum.FLY, OldPlayerStateEnum.ENFIGHT_WALK
    );

    private IPlayerMoveState(OldPlayerStateEnum playerStateEnum, Coordinate start, Direction towards, int millisPerUnit) {
        super(playerStateEnum, start, towards, millisPerUnit);
    }

    @Override
    protected IPlayerState rewindState(PlayerImpl player) {
        return idle(player);
    }


    private void useResource(PlayerImpl player, FootKungFu footKungFu) {
        footKungFu.tryGainExpAndUseResources(player, player::emitEvent);
        if (!footKungFu.canKeep(player)) {
            player.disableFootKungFuNoTip();
        }
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        player.changeState(idle(player));
        player.footKungFu().ifPresent(kf -> useResource(player, kf));
    }

    private IPlayerState idle(PlayerImpl player) {
        return stateEnum() == OldPlayerStateEnum.ENFIGHT_WALK ? PlayerStillState.chillOut(player) : PlayerStillState.idle(player);
    }

    public static IPlayerMoveState moveBy(PlayerImpl player, OldPlayerStateEnum playerStateEnum, Direction direction) {
        if (!MOVE_PLAYER_STATE_ENUMS.contains(playerStateEnum)) {
            throw new IllegalArgumentException("Not a move state: " + playerStateEnum);
        }
        return new IPlayerMoveState(playerStateEnum, player.coordinate(), direction, player.getStateMillis(playerStateEnum));
    }

    public static IPlayerMoveState moveBy(PlayerImpl player, Direction direction) {
        OldPlayerStateEnum playerStateEnum = player.footKungFu().map(kf -> kf.canFly() ? OldPlayerStateEnum.FLY : OldPlayerStateEnum.RUN).orElse(OldPlayerStateEnum.Move);
        return moveBy(player, playerStateEnum, direction);
    }

    @Override
    public OldPlayerStateEnum decideAfterHurtState() {
        return stateEnum() == OldPlayerStateEnum.ENFIGHT_WALK ? OldPlayerStateEnum.FightStand : OldPlayerStateEnum.IDLE;
    }
}
