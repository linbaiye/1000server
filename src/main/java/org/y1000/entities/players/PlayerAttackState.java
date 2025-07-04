package org.y1000.entities.players;

import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.message.PlayerAttackEvent;
import org.y1000.message.PlayerChangeStateEvent;

class PlayerAttackState extends AbstractPlayerState {

    public PlayerAttackState(PlayerImpl player, AttackAction action) {
        super(player, PlayerStateEnum.Attack, action.getMillis());
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    public static void attack(PlayerImpl player) {
        AttackAction action = player.attackKungFu().computeAttackAction();
        PlayerAttributeEvent attack = PlayerAttackEvent.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendEvent(attack);
    }
}
