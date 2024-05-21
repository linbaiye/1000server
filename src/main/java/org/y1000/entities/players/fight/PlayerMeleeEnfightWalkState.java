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
public final class PlayerMeleeEnfightWalkState extends AbstractEnfightWalkState implements AttackableState {

    public PlayerMeleeEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit, PhysicalEntity target) {
        super(start, towards, millisPerUnit, target);
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return new PlayerMeleeCooldownState(REWIND_COOLDOWN, getTarget());
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        attack(player, getTarget());
    }

    public static PlayerMeleeEnfightWalkState move(PlayerImpl player, Direction towards, PhysicalEntity target) {
        return new PlayerMeleeEnfightWalkState(player.coordinate(), towards, player.getStateMillis(State.ENFIGHT_WALK), target);
    }

    @Override
    public Logger logger() {
        return log;
    }
}
