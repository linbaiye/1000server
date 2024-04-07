package org.y1000.entities.players;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveMessage;
import org.y1000.message.TurnMessage;
import org.y1000.message.input.AbstractRightClick;

final class Mover {
    private static final Logger log = LoggerFactory.getLogger(Mover.class);

    public static InputResponseMessage onRightClick(PlayerImpl player, AbstractRightClick click) {
        if (player.CanMoveOneUnit(click.direction())) {
            player.changeState(new PlayerWalkState(click));
            return new InputResponseMessage(click.sequence(), MoveMessage.movingTo(player, click.direction()));
        } else {
            player.changeDirection(click.direction());
            player.changeState(new PlayerIdleState());
            return new InputResponseMessage(click.sequence(), TurnMessage.fromPlayer(player));
        }
    }
}
