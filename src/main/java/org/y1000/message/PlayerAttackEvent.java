package org.y1000.message;

import org.y1000.entities.players.AttackAction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractClientMessageEvent;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.entities.players.event.PlayerEventHandler;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerAttackPacket;

public final class PlayerAttackEvent extends AbstractClientMessageEvent {
    public PlayerAttackEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerAttributeEvent attack(Player player, AttackAction action, int effectId) {
        PlayerAttackPacket attackPacket = PlayerAttackPacket.newBuilder()
                .setId(player.id())
                .setAction(action.value())
                .setDirection(player.direction().value())
                .setEffectSprite(effectId)
                .build();
        return new PlayerAttributeEvent(player, Packet.newBuilder().setAttack(attackPacket).build());
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }
}
