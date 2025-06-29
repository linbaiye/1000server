package org.y1000.entities.players;

import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;

public final class PlayerStandUpState extends IAbstractCreatureState<PlayerImpl> implements IPlayerState {

    public PlayerStandUpState(int totalMillis) {
        super(totalMillis);
    }

    public PlayerStandUpState(PlayerImpl player) {
        this(player.getStateMillis(OldPlayerStateEnum.STANDUP));
    }

    @Override
    public OldPlayerStateEnum stateEnum() {
        return OldPlayerStateEnum.STANDUP;
    }

    @Override
    public boolean canUseFootKungFu() {
        return elapsedMillis() >= totalMillis();
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(PlayerStillState.idle(player));
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.changeState(PlayerStillState.idle(player));
        }
    }
}
