package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractPlayerMessage;
import org.y1000.network.gen.AttributePacket;
import org.y1000.network.gen.Packet;

public final class PlayerAttributeMessage extends AbstractPlayerMessage {
    public PlayerAttributeMessage(Player source, Packet packet) {
        super(source, packet);
    }

    public static AttributePacket makeAttributePacket(Player player) {
        return AttributePacket.newBuilder()
                .setCurLife(player.currentLife())
                .setMaxLife(player.maxLife())
                .setCurInnerPower(player.innerPower())
                .setMaxInnerPower(player.maxInnerPower())
                .setCurPower(player.power())
                .setMaxPower(player.maxPower())
                .setCurOuterPower(player.outerPower())
                .setMaxOuterPower(player.maxOuterPower())
                .setCurEnergy(player.energy())
                .setMaxEnergy(player.maxEnergy())
                .setArmPercent(player.armPercent())
                .setHeadPercent(player.headPercent())
                .setLegPercent(player.legPercent())
                .build();
    }

    public static PlayerAttributeMessage of(Player player) {
        Validate.notNull(player);
        return new PlayerAttributeMessage(player, Packet.newBuilder()
                .setAttribute(makeAttributePacket(player))
                .build());
    }
}
