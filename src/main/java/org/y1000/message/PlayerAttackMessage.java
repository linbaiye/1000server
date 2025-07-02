package org.y1000.message;

import org.y1000.entities.players.AttackAction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttributeMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerAttackPacket;

public final class PlayerAttackMessage extends AbstractInsightPlayerMessage {
    public PlayerAttackMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerAttributeMessage attack(Player player, AttackAction action, int effectId) {
        PlayerAttackPacket attackPacket = PlayerAttackPacket.newBuilder()
                .setId(player.id())
                .setAction(action.value())
                .setDirection(player.direction().value())
                .setEffectSprite(effectId)
                .build();
        return new PlayerAttributeMessage(player, Packet.newBuilder().setAttack(attackPacket).build());
    }
}
