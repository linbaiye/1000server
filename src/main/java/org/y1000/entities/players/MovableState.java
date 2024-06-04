package org.y1000.entities.players;

import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.input.AbstractRightClick;
import org.y1000.message.clientevent.input.RightMouseRelease;
import org.y1000.util.Coordinate;

public interface MovableState {

    Logger logger();

    PlayerState rewindState(PlayerImpl player);

    PlayerState moveState(PlayerImpl player, Direction direction);


    private void handleRightClick(PlayerImpl player, AbstractRightClick rightClick) {
        Coordinate targetCoordinate = player.coordinate().moveBy(rightClick.direction());
        if (!player.realmMap().movable(targetCoordinate)) {
            logger().debug("Destination conflicted, rewind player {} back to {}", player.id(), player.coordinate());
            player.changeDirection(rightClick.direction());
            rewind(player, rightClick.sequence());
        } else {
            PlayerState playerState = player.footKungFu().map(footKungFu ->
                    (PlayerState)PlayerMoveState.moveBy(player, rightClick.direction()))
                    .orElse(moveState(player, rightClick.direction()));
            player.changeState(playerState);
            player.emitEvent(new InputResponseMessage(rightClick.sequence(), MoveEvent.movingTo(player, rightClick.direction())));
        }
    }

    private void rewind(PlayerImpl player, long seq) {
        PlayerState newState = player.footKungFu().map(footKungFu ->
                (PlayerState)PlayerStillState.idle(player)).orElse(rewindState(player));
        player.changeState(newState);
        player.emitEvent(new InputResponseMessage(seq, RewindEvent.of(player)));
    }


    default void move(PlayerImpl player, ClientMovementEvent event) {
        logger().debug("Handling input at state [{}, {}], id {}.", player.state(), player.stateEnum(), event.moveInput().sequence());
        if (!event.happenedAt().equals(player.coordinate())) {
            logger().debug("Rewind because of coordinate mismatch, client: {}, server: {}.", event.happenedAt(), player.coordinate());
            rewind(player, event.moveInput().sequence());
            return;
        }
        if (event.moveInput() instanceof AbstractRightClick rightClick) {
            handleRightClick(player, rightClick);
        } else if (event.moveInput() instanceof RightMouseRelease release) {
            player.emitEvent(new InputResponseMessage(release.sequence(), SetPositionEvent.of(player)));
        }
    }
}
