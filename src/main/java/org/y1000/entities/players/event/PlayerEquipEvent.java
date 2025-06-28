package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.Equipment;
import org.y1000.item.EquipmentType;
import org.y1000.item.Weapon;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

public class PlayerEquipEvent extends Abstract2ClientEntityEvent  {
    private final Packet p;

    public PlayerEquipEvent(Player player, Packet packet) {
        super(player);
        p = packet;
    }

    @Override
    protected Packet buildPacket() {
        return p;
    }

    private static String computeWeaponSprite(boolean male, String wearShape, int value) {
        return  (char) ((male ? (int) 'w' : (int)'j') + value) + wearShape;
    }

    private static String computeArmor(boolean male, String wearShape, int value) {
        return  (char) ((male ? (int) 'n' : (int)'a') + value) + wearShape;
    }

    public static PlayerEquipEvent create(Player player, Equipment equipment) {
        Validate.notNull(player);
        Validate.notNull(equipment);
        PlayerEquipPacket.Builder builder = PlayerEquipPacket.newBuilder()
                .setId(player.id())
                .setColor(equipment.color())
                .setEquipmentType(equipment.equipmentType().value());
        if (equipment.equipmentType() == EquipmentType.WEAPON) {
            builder.setSpritePrefix(computeWeaponSprite(player.isMale(), equipment.wearShape(), equipment.equipmentType().value()));
            builder.setWeaponType(((Weapon)equipment).kungFuType().value());
        } else {
            builder.setSpritePrefix(computeArmor(player.isMale(), equipment.wearShape(), equipment.equipmentType().value()));
        }
        if (equipment.equipmentType() == EquipmentType.WRIST) {
            builder.setPairedSpritePrefix(computeArmor(player.isMale(), equipment.wearShape(), 5));
        }
        return new PlayerEquipEvent(player, Packet.newBuilder().setEquip(builder).build());
    }
}
