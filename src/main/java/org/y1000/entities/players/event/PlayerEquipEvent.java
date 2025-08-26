package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.equipment.Equipment;
import org.y1000.entities.players.equipment.EquipmentType;
import org.y1000.entities.players.equipment.Weapon;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerEquipEvent extends Abstract2VisibleAndSelfMessageEvent {

    public PlayerEquipEvent(Player player, Packet packet) {
        super(player, packet);
    }

    private static String computeWeaponSprite(boolean male, String wearShape) {
        return  (char) ((male ? (int) 'w' : (int)'j')) + wearShape;
    }

    private static String computeNonWeaponSprite(boolean male, String wearShape, int v) {
        return  (char) ((male ? (int) 'n' : (int)'a') + v) + wearShape;
    }

    private static List<String> computeNonWeaponSprites(String prefix) {
        List<String> sprites = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            sprites.add(prefix + i);
        }
        return sprites;
    }

    public static PlayerEquipPacket toEquipPacket(Player player, Equipment equipment) {
        Validate.notNull(equipment);
        PlayerEquipPacket.Builder builder = PlayerEquipPacket.newBuilder()
                .setId(player.id())
                .setName(equipment.name())
                .setColor(equipment.color())
                .setIcon(equipment.icon())
                .setEquipmentType(equipment.equipmentType().value());
        if (equipment.equipmentType() == EquipmentType.WEAPON) {
            String prefix = computeWeaponSprite(player.isMale(), equipment.wearShape());
            builder.setPrefix(prefix);
            AttackKungFuType attackKungFuType = ((Weapon) equipment).kungFuType();
            builder.setWeaponType(attackKungFuType.value());
        } else {
            String prefix = computeNonWeaponSprite(player.isMale(), equipment.wearShape(), equipment.equipmentType().value());
            builder.setPrefix(prefix);
        }
        if (equipment.equipmentType() == EquipmentType.WRIST) {
            String prefix = computeNonWeaponSprite(player.isMale(), equipment.wearShape(), 5);
            builder.setPairedPrefix(prefix);
        }
        return builder.build();
    }

    public static PlayerEquipEvent create(Player player, Equipment equipment) {
        Validate.notNull(player);
        return new PlayerEquipEvent(player, Packet.newBuilder().setEquip(toEquipPacket(player, equipment)).build());
    }


    private static Set<String> noneWeapon(String prefix) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i <= 4; i++) {
            result.add(prefix + i);
        }
        return result;
    }

    private static List<String> weapon(String prefix, AttackKungFuType attackKungFuType) {
        List<String> result = new ArrayList<>();
        result.add(prefix + "0");
        switch (attackKungFuType) {
            case Fist -> result.add(prefix + "1");
            case BLADE, SWORD, THROW -> result.add(prefix + "2");
            case AXE, SPEAR -> result.add(prefix + "3");
            case BOW -> result.add(prefix + "4");
        }
        return result;
    }

    private static void dump() {
        ItemSdbImpl itemSdb = ItemSdbImpl.INSTANCE;
        Set<String> names = itemSdb.columnNames();
        Set<String> items = itemSdb.uniqueIds();
        Set<String> shapes = new HashSet<>();
        for (String i: items) {
            if (itemSdb.getTypeValue(i) != ItemType.EQUIPMENT.value()) {
                continue;
            }
            if ("0".equals(itemSdb.getWearShape(i))) {
                System.out.println(i + " has shape 0");
                continue;
            }
            //System.out.println("----------------------------");
            EquipmentType equipmentType = itemSdb.getEquipmentType(i);
            if (equipmentType !=  EquipmentType.WEAPON) {
                boolean male = itemSdb.isMale(i);
                var str = computeNonWeaponSprite(male, itemSdb.getWearShape(i), itemSdb.getEquipmentType(i).value());
                shapes.addAll(noneWeapon(str));
                if (equipmentType == EquipmentType.WRIST) {
                    str = computeNonWeaponSprite(male, itemSdb.getWearShape(i), 5);
                    shapes.addAll(noneWeapon(str));
                }
            } else {
                var str = computeWeaponSprite(true, itemSdb.getWearShape(i));
                shapes.addAll(weapon(str, itemSdb.getAttackKungFuType(i)));
                str = computeWeaponSprite(false, itemSdb.getWearShape(i));
                shapes.addAll(weapon(str, itemSdb.getAttackKungFuType(i)));
            }
            System.out.println(i + ":" + itemSdb.getWearShape(i));
            /*for (String name : names) {
                if (!StringUtils.isEmpty(itemSdb.get(i, name)))
                    System.out.println(name + ": " + itemSdb.get(i, name));
            }*/
        }
        shapes.forEach(s -> {
            System.out.println("cp /d/work/godot/y1000/Sprites/" + s + ".zip /d/godot_qn/qn_client/sprites");
        });
    }
    public static void main(String[] args) {
        dump();
        //checkDuplicateNames();
    }
}
