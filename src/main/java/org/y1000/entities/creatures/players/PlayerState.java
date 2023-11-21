package org.y1000.entities.creatures.players;

import org.y1000.message.Message;
import org.y1000.message.MoveMessage;

import java.util.Optional;

public interface PlayerState {

    Message sit(Player player);

    Message move(Player player, MoveMessage moveMessage);

    Optional<Message> update(Player player, long delta);
}
