package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.util.Coordinate;

@Slf4j
public final class PlayerRangedEnfightWalkState extends AbstractEnfightWalkState implements AttackableState {

    private final int attackedCounter;

    public PlayerRangedEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit, PhysicalEntity target,
                                        int attackedCounter) {
        super(start, towards, millisPerUnit, target);
        this.attackedCounter = attackedCounter;
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return PlayerRangedCooldownState.cooldown(REWIND_COOLDOWN, getTarget(), attackedCounter);
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        attack(player, getTarget());
    }

    public static PlayerRangedEnfightWalkState walk(PlayerImpl player, Direction direction, PhysicalEntity target, int attackedCounter) {
        return new PlayerRangedEnfightWalkState(player.coordinate(), direction, player.getStateMillis(State.ENFIGHT_WALK), target, attackedCounter);
    }

    @Override
    public Logger logger() {
        return log;
    }
}
