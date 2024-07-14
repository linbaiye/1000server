package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerRightClickAttributePacket;

public class PlayerRightClickAttributeEvent extends AbstractPlayerEvent {
    private final PlayerRightClickAttributePacket pkt;
    public PlayerRightClickAttributeEvent(Player source) {
        super(source);
        this.pkt = rightClickAttributePacket(source);
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
    }


    private PlayerRightClickAttributePacket rightClickAttributePacket(Player player) {
        var am = player.armor();
        var dmg = player.damage();
        return PlayerRightClickAttributePacket.newBuilder()
                .setArmArmor(am.arm())
                .setLegArmor(am.leg())
                .setHeadArmor(am.head())
                .setBodyArmor(am.body())
                .setBodyDamage(dmg.bodyDamage())
                .setHeadDamage(dmg.headDamage())
                .setArmDamage(dmg.armDamage())
                .setLegDamage(dmg.legDamage())
                .setMaxEnergy(player.maxEnergy())
                .setMaxLife(player.maxLife())
                .setMaxPower(player.maxPower())
                .setMaxInnerPower(player.innerPower())
                .setMaxOuterPower(player.outerPower())
                .setAttackSpeed(player.attackSpeed())
                .setRecovery(player.recovery())
                .setAvoidance(player.avoidance())
                .setAge(player.age())
                .build();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setRightClickAttribute(pkt).build();
    }
}
