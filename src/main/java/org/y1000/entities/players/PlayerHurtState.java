package org.y1000.entities.players;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.*;

@Slf4j
public final class PlayerHurtState extends AbstractPlayerState implements PlayerState {

    private final PlayerState returnState;


    private PlayerHurtState(PlayerImpl player, int totalMillis, PlayerState afterHurt) {
        super(player, PlayerStateEnum.Hurt, totalMillis);
        this.returnState = afterHurt;
    }

//    @Override
//    protected void recovery(PlayerImpl player) {
//        returnState.afterHurt(player);
//    }

    public static PlayerHurtState hurt(PlayerImpl player, OldPlayerStateEnum afterHurt) {
//        if (player.creatureState() instanceof PlayerHurtState hurtState) {
//            return new PlayerHurtState(player.getStateMillis(OldPlayerStateEnum.HURT), hurtState.returnState, afterHurt);
//        } else {
//            return new PlayerHurtState(player.getStateMillis(OldPlayerStateEnum.HURT), player.creatureState(), afterHurt);
//        }
        return null;
    }
//
//    @Override
//    public OldPlayerStateEnum decideAfterHurtState() {
//        return this.afterHurtPlayerStateEnum;
//    }

//    @Override
    public void afterHurt(PlayerImpl player) {
        reset();
        player.changeState(this);
    }

    @Override
    public String toString() {
        return playerStateEnum().name();
    }

    @Override
    public void update(int delta) {

    }

}
