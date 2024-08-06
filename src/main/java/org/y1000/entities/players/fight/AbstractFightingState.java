package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

public abstract class AbstractFightingState extends AbstractCreatureState<PlayerImpl> implements PlayerState, MovableState {


    public AbstractFightingState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public PlayerState moveState(PlayerImpl player, Direction direction) {
        return PlayerFightWalkState.walk(player, direction);
    }

    @Override
    public PlayerState rewindState(PlayerImpl player) {
        // The client believes it can move, but we actually can't, change it to cooldown no matter
        // what fighting state we are in.
        return new PlayerCooldownState(player.getStateMillis(State.COOLDOWN));
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public State decideAfterHurtState() {
        return State.COOLDOWN;
    }

    @Override
    public boolean canSitDown() {
        return true;
    }

}
