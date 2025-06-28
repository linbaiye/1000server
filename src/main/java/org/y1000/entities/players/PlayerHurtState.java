package org.y1000.entities.players;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.*;

@Slf4j
public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements IPlayerState {

    private final IPlayerState returnState;

    /**
     * For client to interpolate, perhaps no useless.
     */
    private final PlayerStateEnum afterHurtPlayerStateEnum;

    private PlayerHurtState(int totalMillis, IPlayerState afterHurt, PlayerStateEnum returnPlayerStateEnum) {
        super(totalMillis);
        this.returnState = afterHurt;
        this.afterHurtPlayerStateEnum = returnPlayerStateEnum;
    }

    @Override
    protected void recovery(PlayerImpl player) {
        returnState.afterHurt(player);
    }

    public static PlayerHurtState hurt(PlayerImpl player, PlayerStateEnum afterHurt) {
        if (player.creatureState() instanceof PlayerHurtState hurtState) {
            return new PlayerHurtState(player.getStateMillis(PlayerStateEnum.HURT), hurtState.returnState, afterHurt);
        } else {
            return new PlayerHurtState(player.getStateMillis(PlayerStateEnum.HURT), player.creatureState(), afterHurt);
        }
    }

    @Override
    public PlayerStateEnum decideAfterHurtState() {
        return this.afterHurtPlayerStateEnum;
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
