package org.y1000.message.serverevent;

import org.y1000.message.InputResponseMessage;
import org.y1000.message.LoginSucceededEvent;

public interface PlayerEventHandler extends EntityEventHandler {

    default void handle(LoginSucceededEvent loginMessage) {

    }

    default void handle(InputResponseMessage
                               inputResponseMessage) {

    }

    default void handle(PlayerLeftEvent event) {
    }
}