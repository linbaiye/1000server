package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.input.RightMouseClick;

import java.util.Collections;
import java.util.List;

@Slf4j
final class PlayerIdleState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerState.class);

    private long elapsedMillis = 0;

    private static final long STATE_MILLIS = 3000;

    public PlayerIdleState() {
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        var msg = Mover.moveOrIdle(player, click, click.direction());
        return Collections.singletonList(msg);
    }

    @Override
    public State getState() {
        return State.IDLE;
    }

    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        elapsedMillis += deltaMillis;
        if (elapsedMillis >= STATE_MILLIS)
            elapsedMillis = 0;
        return Collections.emptyList();
    }

    @Override
    public Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis) {
        if (elapsedMillis == 0) {
            return null;
        }
        long interpolationStartAt = (stateStartedAtMillis + (elapsedMillis - player.getRealm().stepMillis()));
        /*return IdleInterpolation.builder()
                .coordinate(player.coordinate())
                .length((short) elapsedMillis)
                .id(player.id())
                .stateStartAtMillis(stateStartedAtMillis)
                .interpolationStart(interpolationStartAt)
                .direction(player.direction())
                .build();*/
        return null;
    }
}
