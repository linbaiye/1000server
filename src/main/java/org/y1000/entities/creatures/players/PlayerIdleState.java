package org.y1000.entities.creatures.players;

import org.y1000.entities.Entity;
import org.y1000.entities.PhysicalEntity;
import org.y1000.message.ConfirmMessage;
import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.SetCoordinateMessage;

import java.util.Optional;

public class PlayerIdleState implements PlayerState {

    public PlayerIdleState() {
    }

    @Override
    public Message sit(Player player) {
        return null;
    }

    @Override
    public Message move(Player player, MoveMessage moveMessage) {
        var nextCoordinate = player.coordinate().moveBy(moveMessage.direction());
        if (player.getRealm().hasPhysicalEntityAt(nextCoordinate)) {
            return new SetCoordinateMessage(moveMessage.messageId(), player.coordinate());
        } else {
            player.changeDirection(moveMessage.direction());
            player.changeState(new PlayerWalkState(System.currentTimeMillis(), player.coordinate(), moveMessage.direction()));
            return moveMessage;
        }
    }

    @Override
    public Optional<Message> update(Player player, long delta) {
        return Optional.empty();
    }

}
