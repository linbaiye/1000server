package org.y1000.entities.players;

import org.y1000.message.PlayerChangeStateEvent;

final class PlayerMeleeState extends AbstractPlayerAttackState {

    public PlayerMeleeState(PlayerImpl player, AttackAction action) {
        super(player, PlayerStateEnum.Attack, action.getMillis());
    }

    @Override
    public void update(int delta) {
        if (player().tryCombatStrike(delta)) {
            return;
        }
        if (elapse(delta)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }
}
