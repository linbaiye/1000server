package org.y1000.entities.players.event;

import org.y1000.entities.players.Armor;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.AttributeEquipPacket;
import org.y1000.network.gen.EquippedItemPacket;
import org.y1000.network.gen.Packet;

import java.util.ArrayList;
import java.util.List;

public class AttributeEquipmentMessage extends Abstract2PlayerMessageEvent {

    public AttributeEquipmentMessage(Player player, Packet packet) {
        super(player, packet);
    }

    private static String formatAttribute(int v) {
        return (v / 100) + "." + String.format("%02d", v % 100);
    }

    private static List<EquippedItemPacket> equippedItemPacketList(Player player) {
        List<EquippedItemPacket> packets = new ArrayList<>();
        player.getEquipments().forEach(e -> packets.add(EquippedItemPacket.newBuilder().setName(e.name()).setType(e.equipmentType().value())
                .setColor(e.color()).setIcon(e.icon()).build()));
        return packets;
    }

    public static AttributeEquipmentMessage of(Player player) {
        Damage damage = player.damage();
        Armor armor = player.armor();
        // Order matters.
        AttributeEquipPacket packet = AttributeEquipPacket.newBuilder()
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
                .addAllEquipments(equippedItemPacketList(player))
                .build();
        return new AttributeEquipmentMessage(player, Packet.newBuilder().setAttributeEquip(packet).build());
    }
}
