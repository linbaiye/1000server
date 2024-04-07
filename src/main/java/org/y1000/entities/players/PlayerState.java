package org.y1000.entities.players;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMousePressedMotion;
import org.y1000.message.input.RightMouseRelease;

import java.util.Collections;
import java.util.List;

interface PlayerState {

    List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click);

    default List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        return Collections.emptyList();
    }

    List<I2ClientMessage> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event);

    default List<I2ClientMessage> OnRightMousePressedMotion(PlayerImpl player, RightMousePressedMotion motion) {
        return Collections.emptyList();
    }

    State getState();

    List<I2ClientMessage> update(PlayerImpl player, long deltaMillis);

    Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis);
}

