package org.y1000.entities.players.event;

import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.AttributeEquipPacket;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取装备和属性。
 */
public class AttributeEquipmentMessage extends Abstract2PlayerMessageEvent {

    public AttributeEquipmentMessage(Player player, Packet packet) {
        super(player, packet);
    }

    private static String formatAttribute(int v) {
        return (v / 100) + "." + String.format("%02d", v % 100);
    }

    private static AttributeEquipPacket.Builder attributeBuilder(Player player) {
        Damage damage = player.damage();
        Armor armor = player.armor();
        // Order matters.
        return AttributeEquipPacket.newBuilder()
                .addAttributes(String.valueOf(player.attackSpeed()))
                .addAttributes(String.valueOf(player.avoidance()))
                .addAttributes("0")
                .addAttributes(String.valueOf(damage.bodyDamage()))
                .addAttributes(String.valueOf(damage.headDamage()))
                .addAttributes(String.valueOf(damage.armDamage()))
                .addAttributes(String.valueOf(damage.legDamage()))
                .addAttributes(formatAttribute(player.maxInnerPower()))
                .addAttributes(formatAttribute(player.maxOuterPower()))
                .addAttributes(formatAttribute(player.maxPower()))
                .addAttributes(formatAttribute(player.maxLife()))
                .addAttributes("-")
                .addAttributes(String.valueOf(player.recovery()))
                .addAttributes("-")
                .addAttributes(String.valueOf(armor.body()))
                .addAttributes(String.valueOf(armor.head()))
                .addAttributes(String.valueOf(armor.arm()))
                .addAttributes(String.valueOf(armor.leg()))
                .addAttributes("-")
                .addAttributes(formatAttribute(player.totalAttribute()))
                .addAttributes(String.valueOf(PlayerShoutEvent.ComputeShoutLevel(player)))
                .setAge(formatAttribute(player.age()))
                .setName(player.viewName())
                .setMale(player.isMale());
    }

    private static List<PlayerEquipPacket> equippedItemPacketList(Player player) {
        List<PlayerEquipPacket> packets = new ArrayList<>();
        player.getEquipments().forEach(e -> packets.add(PlayerEquipEvent.toEquipPacket(player,e)));
        return packets;
    }

    public static AttributeEquipmentMessage quietly(Player player) {
        AttributeEquipPacket packet = attributeBuilder(player).setQuietly(true).build();
        return new AttributeEquipmentMessage(player, Packet.newBuilder().setAttributeEquip(packet).build());
    }

    public static AttributeEquipmentMessage of(Player player) {
        AttributeEquipPacket packet = attributeBuilder(player)
                .addAllEquipments(equippedItemPacketList(player))
                .setQuietly(false)
                .build();
        return new AttributeEquipmentMessage(player, Packet.newBuilder().setAttributeEquip(packet).build());
    }
}
