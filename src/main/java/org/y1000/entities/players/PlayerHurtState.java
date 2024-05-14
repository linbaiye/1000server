package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {
    private final CreatureState<PlayerImpl> afterRecover;


    public PlayerHurtState(int totalMillis, CreatureState<PlayerImpl> afterRecover) {
        super(totalMillis);
        this.afterRecover = afterRecover;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        player.changeState(afterRecover);
    }

    public static PlayerHurtState attackedBy(PlayerImpl player, CreatureState<PlayerImpl> after) {
        int stateMillis = player.getStateMillis(State.HURT);
        return new PlayerHurtState(stateMillis, after);
    }
}
