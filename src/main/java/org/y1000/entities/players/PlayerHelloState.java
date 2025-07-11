package org.y1000.entities.players;

import org.y1000.message.PlayerChangeStateEvent;

final class PlayerHelloState extends AbstractPlayerState {

    static final int StateMillis = 750;

    public PlayerHelloState(PlayerImpl player) {
        super(player, PlayerStateEnum.Hello, StateMillis);
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            player().changeState(PlayerStandState.idle(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    @Override
    public void handleAfterHurt() {
        player().changeState(PlayerStandState.idle(player()));
        player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
    }
}
