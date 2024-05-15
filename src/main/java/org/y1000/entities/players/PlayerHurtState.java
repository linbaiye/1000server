package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements AttackableState, PlayerState {

    @FunctionalInterface
    public interface AfterHurtAction {
        void apply(PlayerImpl player);
    }

    private final AfterHurtAction afterHurtAction;

    public PlayerHurtState(Creature attacker, int totalMillis, AfterHurtAction afterHurt) {
        super(totalMillis, attacker);
        this.afterHurtAction = afterHurt;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        afterHurtAction.apply(player);
    }
}
