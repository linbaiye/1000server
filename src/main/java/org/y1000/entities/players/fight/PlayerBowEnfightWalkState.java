package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.util.Coordinate;

public final class PlayerBowEnfightWalkState extends AbstractEnfightWalkState implements AttackableState {

    private final int attackedCounter;

    public PlayerBowEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit, Entity target,
                                     int attackedCounter) {
        super(start, towards, millisPerUnit, target);
        this.attackedCounter = attackedCounter;
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return PlayerBowCooldownState.cooldown(REWIND_COOLDOWN, getTarget(), attackedCounter);
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        attack(player, getTarget());
    }

    public static PlayerBowEnfightWalkState walk(PlayerImpl player, Direction direction, Entity target, int attackedCounter) {
        return new PlayerBowEnfightWalkState(player.coordinate(), direction, player.getStateMillis(State.ENFIGHT_WALK), target, attackedCounter);
    }
}
