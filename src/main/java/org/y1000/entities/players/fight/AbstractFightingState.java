package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.IPlayerState;

public abstract class AbstractFightingState extends IAbstractCreatureState<PlayerImpl> implements IPlayerState, MovableState {


    public AbstractFightingState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public IPlayerState moveState(PlayerImpl player, Direction direction) {
        return PlayerFightWalkState.walk(player, direction);
    }

    @Override
    public IPlayerState rewindState(PlayerImpl player) {
        // The client believes it can move, but we actually can't, change it to cooldown no matter
        // what fighting state we are in.
        return new PlayerCooldownState(player.getStateMillis(OldPlayerStateEnum.FightStand));
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public OldPlayerStateEnum decideAfterHurtState() {
        return OldPlayerStateEnum.FightStand;
    }

    @Override
    public boolean canSitDown() {
        return true;
    }

}
