package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {


    @FunctionalInterface
    public interface AfterHurtAction {
        void apply(PlayerImpl player);
    }

    private final AfterHurtAction afterHurtAction;

    public PlayerHurtState(int totalMillis, AfterHurtAction afterHurt) {
        super(totalMillis);
        this.afterHurtAction = afterHurt;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        afterHurtAction.apply(player);
    }

    public static PlayerHurtState hurt(PlayerImpl player) {
        return new PlayerHurtState(player.getStateMillis(State.HURT), player.state()::afterHurt);
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        reset();
        player.changeState(this);
    }

    @Override
    public String toString() {
        return stateEnum().name();
    }
}
