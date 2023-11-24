package org.y1000.entities.creatures.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;

import java.util.Optional;

@Slf4j
final class PlayerIdleState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerState.class);

    public static final PlayerIdleState INSTANCE = new PlayerIdleState();

    public PlayerIdleState() {
    }

    @Override
    public Optional<Message> sit(PlayerImpl player) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> move(PlayerImpl player, MoveMessage moveMessage) {
        var nextCoordinate = player.coordinate().moveBy(moveMessage.direction());
        player.changeDirection(moveMessage.direction());
        if (player.getRealm().hasPhysicalEntityAt(nextCoordinate)) {
            LOGGER.warn("Player {} trying to move into unmovable coordinate.", player.id());
            return Optional.of(PositionMessage.fromCreature(player));
        }
        player.changeState(new PlayerWalkState());
        return Optional.of(MoveMessage.fromCreature(player));
    }

    @Override
    public Optional<Message> update(PlayerImpl player, long deltaMillis) {
        return Optional.empty();
    }

}
