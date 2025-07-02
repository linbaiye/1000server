package org.y1000.entities.players;

import org.y1000.entities.players.event.PlayerAttributeMessage;
import org.y1000.message.PlayerAttackMessage;
import org.y1000.message.PlayerChangeStateMessage;

class PlayerAttackState extends AbstractPlayerState {

    public PlayerAttackState(PlayerImpl player, AttackAction action) {
        super(player, PlayerStateEnum.Attack, action.getMillis());
    }

    @Override
    public void update(int delta) {
        if (elapse(delta)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendMessage(PlayerChangeStateMessage.allVisible(player()));
        }
    }

    public static void attack(PlayerImpl player) {
        AttackAction action = player.attackKungFu().computeAttackAction();
        PlayerAttributeMessage attack = PlayerAttackMessage.attack(player, action, player.attackKungFu().computeEffectId());
        player.sendMessage(attack);
    }
}
