package org.y1000.entities.players;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;

import java.util.List;

interface PlayerState {


    List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click);

    List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release);

    State getState();

    List<I2ClientMessage> update(PlayerImpl player, long deltaMillis);

    Interpolation snapshot();

}

