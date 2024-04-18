package org.y1000.message.serverevent;

import org.y1000.message.InputResponseMessage;
import org.y1000.message.LoginMessage;

public interface PlayerEventHandler extends EntityEventHandler {

    default void handle(LoginMessage loginMessage) {

    }

    default void handle(InputResponseMessage
                               inputResponseMessage) {

    }
}