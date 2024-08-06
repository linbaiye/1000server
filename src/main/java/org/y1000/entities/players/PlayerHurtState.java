package org.y1000.entities.players;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.*;

@Slf4j
public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {

    private final PlayerState returnState;

    /**
     * For client to interpolate, perhaps no useless.
     */
    private final State afterHurtState;

    private PlayerHurtState(int totalMillis, PlayerState afterHurt, State returnState) {
        super(totalMillis);
        this.returnState = afterHurt;
        this.afterHurtState = returnState;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        returnState.afterHurt(player);
    }

    public static PlayerHurtState hurt(PlayerImpl player, State afterHurt) {
        if (player.state() instanceof PlayerHurtState hurtState) {
            return new PlayerHurtState(player.getStateMillis(State.HURT), hurtState.returnState, afterHurt);
        } else {
            return new PlayerHurtState(player.getStateMillis(State.HURT), player.state(), afterHurt);
        }
    }

    @Override
    public State decideAfterHurtState() {
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
