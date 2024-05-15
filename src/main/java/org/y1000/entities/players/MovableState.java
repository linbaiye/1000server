package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.input.AbstractRightClick;
import org.y1000.message.clientevent.input.RightMouseRelease;
import org.y1000.util.Coordinate;

interface MovableState {

    default CreatureState<PlayerImpl> stateForRewind(PlayerImpl player) {
        return stateForStopMoving(player);
    }

    CreatureState<PlayerImpl> stateForStopMoving(PlayerImpl player);

    CreatureState<PlayerImpl> stateForMove(PlayerImpl player, Direction direction);

    private void handleRightClick(PlayerImpl player, AbstractRightClick rightClick) {
        Coordinate targetCoordinate = player.coordinate().moveBy(rightClick.direction());
        if (!player.realmMap().movable(targetCoordinate)) {
            player.changeDirection(rightClick.direction());
            player.changeState(stateForStopMoving(player));
            player.emitEvent(new InputResponseMessage(rightClick.sequence(), SetPositionEvent.of(player)));
        } else {
            player.changeState(stateForMove(player, rightClick.direction()));
            player.emitEvent(new InputResponseMessage(rightClick.sequence(), MoveEvent.movingTo(player, rightClick.direction())));
        }
    }

    private void rewind(PlayerImpl player, ClientMovementEvent event) {
        player.changeState(stateForRewind(player));
        player.clearEventQueue();
        player.emitEvent(new InputResponseMessage(event.moveInput().sequence(), RewindEvent.of(player)));
    }

    private void handleRelease(PlayerImpl player, RightMouseRelease release) {
        player.changeState(stateForStopMoving(player));
        player.emitEvent(new InputResponseMessage(release.sequence(), SetPositionEvent.of(player)));
    }

    default void move(PlayerImpl player, ClientMovementEvent event) {
        if (!event.happenedAt().equals(player.coordinate())) {
            rewind(player, event);
            return;
        }
        if (event.moveInput() instanceof AbstractRightClick rightClick) {
            handleRightClick(player, rightClick );
        } else if (event.moveInput() instanceof RightMouseRelease release) {
            handleRelease(player, release);
        }
    }
}
