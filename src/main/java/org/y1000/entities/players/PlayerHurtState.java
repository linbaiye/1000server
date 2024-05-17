package org.y1000.entities.players;
import lombok.Getter;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {

    @Getter
    private final State interruptedState;
    @Getter
    private final int interruptedElapse;

    @FunctionalInterface
    public interface AfterHurtAction {
        void apply(PlayerImpl player);
    }

    private final AfterHurtAction afterHurtAction;

    public PlayerHurtState(int totalMillis, State interruptedState, int interruptedElapse, AfterHurtAction afterHurt) {
        super(totalMillis);
        this.interruptedState = interruptedState;
        this.interruptedElapse = interruptedElapse;
        this.afterHurtAction = afterHurt;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        afterHurtAction.apply(player);
    }

    public static PlayerHurtState interruptCurrentState(PlayerImpl player) {
        return new PlayerHurtState(player.getStateMillis(State.HURT), player.stateEnum(), player.state().elapsedMillis(), player.state()::afterHurt);
    }
}
