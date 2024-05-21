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
public final class PlayerBowEnfightWalkState extends AbstractEnfightWalkState implements AttackableState {

    private final int attackedCounter;

    public PlayerBowEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit, PhysicalEntity target,
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
        if (player.cooldown() > 0) {
            player.changeState(PlayerBowCooldownState.cooldown(player, getTarget(), attackedCounter));
        } else {
            rangedAttack(player, getTarget(), attackedCounter);
        }
    }

    public static PlayerBowEnfightWalkState walk(PlayerImpl player, Direction direction, PhysicalEntity target, int attackedCounter) {
        return new PlayerBowEnfightWalkState(player.coordinate(), direction, player.getStateMillis(State.ENFIGHT_WALK), target, attackedCounter);
    }

    @Override
    public Logger logger() {
        return log;
    }
}
