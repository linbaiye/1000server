package org.y1000.message;

import java.util.Optional;

public interface MessageHandler {

    Optional<Message> handle(MoveMessage moveMessage);

    Optional<Message> handle(StopMoveMessage moveMessage);

    Optional<Message> handle(Message message);

}
