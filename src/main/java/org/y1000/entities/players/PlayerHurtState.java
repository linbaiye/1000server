package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {

    @FunctionalInterface
    public interface AfterHurtAction {
        void apply(PlayerImpl player);
    }

    private final AfterHurtAction afterHurtAction;

    private final State afterHurtState;

    private PlayerHurtState(int totalMillis, AfterHurtAction afterHurt, State returnState) {
        super(totalMillis);
        this.afterHurtAction = afterHurt;
        this.afterHurtState = returnState;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        afterHurtAction.apply(player);
    }

    public static PlayerHurtState hurt(PlayerImpl player, State afterHurt) {
        return new PlayerHurtState(player.getStateMillis(State.HURT), player.state()::afterHurt, afterHurt);
    }

    @Override
    public State decideAfterHurtState(PlayerImpl player) {
        return this.afterHurtState;
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
