package org.y1000.entities.players;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.input.AbstractRightClick;

final class Mover {
    private static final Logger log = LoggerFactory.getLogger(Mover.class);

    public static InputResponseMessage onRightClick(PlayerImpl player, AbstractRightClick click) {
        if (player.CanMoveOneUnit(click.direction())) {
            player.changeState(PlayerMoveState.move(player, click));
            return new InputResponseMessage(click.sequence(), MoveEvent.movingTo(player, click.direction()));
        } else {
            log.debug("Can't move to {}", player.coordinate().moveBy(click.direction()));
            player.changeDirection(click.direction());
            player.changeState(new PlayerIdleState());
            return new InputResponseMessage(click.sequence(), SetPositionEvent.fromPlayer(player));
        }
    }
}
