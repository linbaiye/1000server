package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;

@Slf4j
public final class PlayerSitDownState extends IAbstractCreatureState<PlayerImpl> implements IPlayerState {

    public PlayerSitDownState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public OldPlayerStateEnum stateEnum() {
        return OldPlayerStateEnum.SIT;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() >= totalMillis()) {
            return;
        }
        elapse(delta);
    }


    @Override
    public boolean canStandUp() {
        return elapsedMillis() >= totalMillis();
    }

    @Override
    public boolean canUseFootKungFu() {
        return elapsedMillis() >= totalMillis();
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        reset();
        player.changeState(this);
    }

    public static PlayerSitDownState sit(PlayerImpl player) {
        return new PlayerSitDownState(player.getStateMillis(OldPlayerStateEnum.SIT));
    }

    @Override
    public OldPlayerStateEnum decideAfterHurtState() {
        return OldPlayerStateEnum.SIT;
    }
}
