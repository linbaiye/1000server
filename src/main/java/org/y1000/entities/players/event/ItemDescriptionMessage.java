package org.y1000.entities.players.event;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.players.Player;
import org.y1000.item.Equipment;
import org.y1000.item.EquipmentType;
import org.y1000.item.Item;
import org.y1000.kungfu.KungFu;
import org.y1000.network.gen.ItemDescriptionPacket;
import org.y1000.network.gen.Packet;

public class ItemDescriptionMessage extends Abstract2PlayerMessageEvent {
    public ItemDescriptionMessage(Player source, Packet packet) {
        super(source, packet);
    }

    private static Packet build(String name, int index, int type, String desc) {
        String text = StringUtils.isEmpty(desc) ? name : name + "\n" + desc;
        ItemDescriptionPacket packet = ItemDescriptionPacket.newBuilder()
                .setText(text)
                .setIndex(index)
                .setType(type)
                .build();
        return Packet.newBuilder().setItemDescription(packet).build();
    }

    public static ItemDescriptionMessage item(Player player, int slot, Item item) {
        return new ItemDescriptionMessage(player, build(item.name(), slot, 0, item.description()));
    }

    public static ItemDescriptionMessage kungfu(Player player, int slot, KungFu kungFu) {
        return new ItemDescriptionMessage(player, build(kungFu.name(), slot, 1, kungFu.detailText()));
    }

    public static ItemDescriptionMessage equip(Player player, EquipmentType type, Equipment item) {
        return new ItemDescriptionMessage(player, build(item.name(), type.value(), 2, item.description()));
    }
}
