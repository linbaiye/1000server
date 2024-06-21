package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.AbstractPlayerMoveState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.util.Coordinate;

public final class PlayerFightWalkState extends AbstractPlayerMoveState {

    public PlayerFightWalkState(Coordinate start, Direction towards, int millisPerUnit) {
        super(State.ENFIGHT_WALK, start, towards, millisPerUnit);
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return new PlayerCooldownState(player.getStateMillis(State.COOLDOWN));
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        player.attackKungFu().attackAgain(player);
    }

    public static PlayerFightWalkState walk(PlayerImpl player, Direction towards) {
        return new PlayerFightWalkState(player.coordinate(), towards, player.getStateMillis(State.ENFIGHT_WALK));
    }

    @Override
    public State decideAfterHurtState() {
        return State.COOLDOWN;
    }

}
