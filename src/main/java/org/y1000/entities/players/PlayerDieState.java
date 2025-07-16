package org.y1000.entities.players;

import org.y1000.message.PlayerChangeStateEvent;

final class PlayerDieState extends AbstractPlayerState {

    static final int DieMillis = 30000;

    public PlayerDieState(PlayerImpl player) {
        super(player, PlayerStateEnum.Die, DieMillis);
    }

    @Override
    public void update(int delta) {
        if (!elapse(delta)) {
            return;
        }
        player().changeState(PlayerStandState.idle(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }

    public static PlayerDieState of(PlayerImpl player) {
        return new PlayerDieState(player);
    }

}
