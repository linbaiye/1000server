package org.y1000.entities.players;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.entities.Direction;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveMessage;
import org.y1000.message.TurnMessage;
import org.y1000.message.input.InputMessage;

final class Mover {
    private static final Logger log = LoggerFactory.getLogger(Mover.class);

    public static InputResponseMessage moveAndRespond(PlayerImpl player, long sequence, Direction direction) {
        var next = player.coordinate().moveBy(direction);
        player.changeDirection(direction);
        return player.getRealm().canMoveTo(next) ?
            new InputResponseMessage(sequence, MoveMessage.fromPlayer(player)) :
            new InputResponseMessage(sequence, TurnMessage.fromPlayer(player));
    }

    public static InputResponseMessage moveOrIdle(PlayerImpl player, InputMessage inputMessage, Direction direction) {
        player.changeDirection(direction);
        if (player.CanMoveOneUnit(direction)) {
            player.changeState(new PlayerWalkState(inputMessage));
            return new InputResponseMessage(inputMessage.sequence(), MoveMessage.fromPlayer(player));
        } else {
            player.changeState(new PlayerIdleState());
            return new InputResponseMessage(inputMessage.sequence(), TurnMessage.fromPlayer(player));
        }
    }
}
