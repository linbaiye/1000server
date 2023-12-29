package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;

import java.util.Collections;
import java.util.List;

@Slf4j
final class PlayerIdleState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerState.class);

    private long elapsedMillis = 0;


    public PlayerIdleState() {
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        var nextCoordinate = player.coordinate().moveBy(click.direction());
        player.changeDirection(click.direction());
        if (!player.getRealm().canMoveTo(nextCoordinate)) {
            return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, click.sequence()));
        }
        player.changeState(new PlayerWalkState(click));
        return Collections.emptyList();
    }

    @Override
    public State getState() {
        return State.IDLE;
    }

    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        elapsedMillis += deltaMillis;
        return Collections.emptyList();
    }

    @Override
    public Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis) {
        if (elapsedMillis == 0) {
            return null;
        }
        long interpolationStartAt = (stateStartedAtMillis + (elapsedMillis - player.getRealm().stepMillis()));
        return IdleInterpolation.builder()
                .coordinate(player.coordinate())
                .length((short) elapsedMillis)
                .id(player.id())
                .stateStartAtMillis(stateStartedAtMillis)
                .interpolationStart(interpolationStartAt)
                .direction(player.direction())
                .build();
    }
}
