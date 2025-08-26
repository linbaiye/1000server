package org.y1000.entities.players.event;

import org.y1000.entities.players.AttackAction;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerAttackPacket;

public final class PlayerAttackEvent extends Abstract2VisibleAndSelfMessageEvent  {
    public PlayerAttackEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerAttackEvent attack(Player player, AttackAction action, String effectId) {
        PlayerAttackPacket attackPacket = PlayerAttackPacket.newBuilder()
                .setId(player.id())
                .setAction(action.value())
                .setDirection(player.direction().value())
                .setEffectSprite(effectId)
                .build();
        return new PlayerAttackEvent(player, Packet.newBuilder().setAttack(attackPacket).build());
    }
}
