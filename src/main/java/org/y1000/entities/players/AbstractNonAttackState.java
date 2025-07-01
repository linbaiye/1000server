package org.y1000.entities.players;

abstract class AbstractNonAttackState extends AbstractPlayerState {
    public AbstractNonAttackState(PlayerImpl player, PlayerStateEnum stateEnum, int stateMillis) {
        super(player, stateEnum, stateMillis);
    }
}
