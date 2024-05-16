package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

final class PlayerEnfightWalkState extends AbstractPlayerMoveState {
    private final Entity target;

    private PlayerEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit,
                                   Entity target) {
        super(State.ENFIGHT_WALK, start, towards, millisPerUnit);
        this.target = target;
    }

    @Override
    protected PlayerState stopMovingState(PlayerImpl player) {
        return new PlayerEnfightState(player.getStateMillis(State.COOLDOWN), target);
    }

    public static PlayerEnfightWalkState move(PlayerImpl player, Direction towards, Entity target) {
        return new PlayerEnfightWalkState(player.coordinate(), towards, player.getStateMillis(State.ENFIGHT_WALK), target);
    }
}
