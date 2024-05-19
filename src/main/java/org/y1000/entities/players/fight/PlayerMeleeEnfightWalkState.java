package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.util.Coordinate;

public final class PlayerMeleeEnfightWalkState extends AbstractEnfightWalkState implements AttackableState {

    public PlayerMeleeEnfightWalkState(Coordinate start, Direction towards, int millisPerUnit, Entity target) {
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

    public static PlayerMeleeEnfightWalkState move(PlayerImpl player, Direction towards, Entity target) {
        return new PlayerMeleeEnfightWalkState(player.coordinate(), towards, player.getStateMillis(State.ENFIGHT_WALK), target);
    }
}
