package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.input.AbstractRightClick;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMousePressedMotion;

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
        var msg = Mover.onRightClick(player, click);
        return Collections.singletonList(msg);
    }

    @Override
    public List<I2ClientMessage> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event) {
        if (event.inputMessage() instanceof AbstractRightClick rightClick) {
            return Collections.singletonList(Mover.onRightClick(player, rightClick));
        }
        return Collections.emptyList();
    }

    @Override
    public List<I2ClientMessage> OnRightMousePressedMotion(PlayerImpl player, RightMousePressedMotion motion) {
        var msg = Mover.onRightClick(player, motion);
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
