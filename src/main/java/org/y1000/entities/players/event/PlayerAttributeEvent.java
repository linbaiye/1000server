package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.AttributePacket;
import org.y1000.network.gen.Packet;

public final class PlayerAttributeEvent extends AbstractPlayerEvent{
    public PlayerAttributeEvent(Player source) {
        super(source);
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttribute(makeAttributePacket(player()))
                .build();
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
}
