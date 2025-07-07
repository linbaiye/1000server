package org.y1000.entities.players;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class PlayerHurtState extends AbstractPlayerState implements PlayerState {

    private final PlayerState returnState;

    private PlayerHurtState(PlayerImpl player, PlayerState afterHurt) {
        super(player, PlayerStateEnum.Hurt, 280);
        this.returnState = afterHurt;
    }

    @Override
    public void handleAfterHurt() {
        returnState.handleAfterHurt();
    }

    @Override
    public void update(int delta) {
        if (elapse(delta))
            returnState.handleAfterHurt();
    }

    public static PlayerHurtState create(PlayerImpl player, PlayerState currentState) {
        if (currentState instanceof PlayerHurtState hurtState) {
            return new PlayerHurtState(player, hurtState.returnState);
        } else {
            return new PlayerHurtState(player, currentState);
        }
    }
}
