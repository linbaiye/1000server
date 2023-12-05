package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.CreatureManager;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.util.Coordinate;

import java.util.Optional;

@Slf4j
final class PlayerWalkState implements PlayerState {

    static final long MILLIS_PER_UNIT = 900;

    private long elapsedMilli;

    private InputMessage trigger;

    private InputMessage lastReceivedInput;

    public PlayerWalkState(InputMessage trigger) {
        elapsedMilli = 0;
        this.trigger = trigger;
    }


    @Override
    public Optional<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        lastReceivedInput = click;
        return Optional.empty();
    }

    @Override
    public Optional<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        lastReceivedInput = release;
        return Optional.empty();
    }

    @Override
    public State getState() {
        return State.WALK;
    }


    private void updatePlayerState(PlayerImpl player) {
        if (lastReceivedInput instanceof RightMouseRelease) {
            player.changeState(PlayerIdleState.INSTANCE);
        } else if (lastReceivedInput == null) {
            if (trigger instanceof RightMouseClick click) {
                Coordinate next = player.coordinate().moveBy(click.direction());
                if (!player.getRealm().canMoveTo(next)) {
                    log.debug("{} not movable, changing back to idle.", next);
                    player.changeState(PlayerIdleState.INSTANCE);
                }
            }
        } else if (lastReceivedInput instanceof RightMouseClick click) {
            player.changeDirection(click.direction());
        }
    }


    @Override
    public Optional<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        elapsedMilli += deltaMillis;
        if (elapsedMilli < MILLIS_PER_UNIT) {
            return Optional.empty();
        }
        elapsedMilli -= MILLIS_PER_UNIT;
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        log.debug("Changing coordinate to {}.", newCoordinate);
        player.changeCoordinate(newCoordinate);
        long triggerSequence = trigger.sequence();
        updatePlayerState(player);
        if (lastReceivedInput != null) {
            trigger = lastReceivedInput;
            lastReceivedInput = null;
        }
        UpdateMovementStateMessage message = UpdateMovementStateMessage.fromPlayer(player, triggerSequence);
        return Optional.of(message);
    }
}
