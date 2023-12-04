package org.y1000.entities.players;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;

import java.util.Optional;

interface PlayerState {


    Optional<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click);

    Optional<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release);

    State getState();

    Optional<I2ClientMessage> update(PlayerImpl player, long deltaMillis);

}

