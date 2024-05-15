package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements AttackableState {

    public PlayerHurtState(Creature attacker, int totalMillis, AfterHurtAction<PlayerImpl> afterHurt) {
        super(totalMillis, afterHurt, attacker);
    }
}
