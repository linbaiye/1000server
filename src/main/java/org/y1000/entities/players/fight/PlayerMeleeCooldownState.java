package org.y1000.entities.players.fight;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

@Slf4j
public final class PlayerMeleeCooldownState extends AbstractCooldownState {

    public PlayerMeleeCooldownState(int length, Entity target) {
        super(length, target);
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return new PlayerMeleeCooldownState(player.getStateMillis(stateEnum()), getTarget());
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerMeleeEnfightWalkState.move(player, direction, getTarget());
    }
}
