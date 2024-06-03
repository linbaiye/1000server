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

    default PlayerState stateForRewind(PlayerImpl player) {
        return stateForStuckMoving(player);
    }

    default PlayerState stateForStuckMoving(PlayerImpl player) {
        return player.state();
    }

    PlayerState stateForMove(PlayerImpl player, Direction direction);

    private void handleRightClick(PlayerImpl player, AbstractRightClick rightClick) {
        Coordinate targetCoordinate = player.coordinate().moveBy(rightClick.direction());
        if (!player.realmMap().movable(targetCoordinate)) {
            player.changeDirection(rightClick.direction());
            PlayerState playerState = player.footKungFu().map(footKungFu ->
                            (PlayerState)PlayerStillState.idle(player)).orElse(stateForStuckMoving(player));
            player.changeState(playerState);
            player.emitEvent(new InputResponseMessage(rightClick.sequence(), RewindEvent.of(player)));
        } else {
            PlayerState playerState = player.footKungFu().map(footKungFu ->
                    (PlayerState)PlayerMoveState.moveBy(player, rightClick.direction()))
                    .orElse(stateForMove(player, rightClick.direction()));
            player.changeState(playerState);
            player.emitEvent(new InputResponseMessage(rightClick.sequence(), MoveEvent.movingTo(player, rightClick.direction())));
        }
    }

    private void rewind(PlayerImpl player, ClientMovementEvent event) {
        PlayerState newState = player.footKungFu().map(footKungFu ->
                (PlayerState)PlayerStillState.idle(player)).orElse(stateForRewind(player));
        logger().debug("Rewind to state {}, server coordinate {}, client coordinate {} for id {}.", newState.stateEnum(), player.coordinate(), event.happenedAt(), event.moveInput().sequence());
        player.changeState(newState);
        player.clearEventQueue();
        player.emitEvent(new InputResponseMessage(event.moveInput().sequence(), RewindEvent.of(player)));
    }

    private void handleRelease(PlayerImpl player, RightMouseRelease release) {
        player.changeState(stateForStuckMoving(player));
        player.emitEvent(new InputResponseMessage(release.sequence(), SetPositionEvent.of(player)));
    }

    default void move(PlayerImpl player, ClientMovementEvent event) {
        logger().debug("Handling input at state [{}, {}], id {}.", player.state(), player.stateEnum(), event.moveInput().sequence());
        if (!event.happenedAt().equals(player.coordinate())) {
            rewind(player, event);
            return;
        }
        if (event.moveInput() instanceof AbstractRightClick rightClick) {
            handleRightClick(player, rightClick);
        } else if (event.moveInput() instanceof RightMouseRelease release) {
            handleRelease(player, release);
        }
    }
}
