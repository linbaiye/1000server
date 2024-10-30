package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

@Data
@Entity
@Table(name = "player_item")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerItemPo {

    private String name;

    @EmbeddedId
    private SlotKey itemKey;

    private long number;

    private int color;

    public static PlayerItemPo toInventoryItem(long playerId, int slot, Item item) {
        if (item instanceof StackItem stackItem) {
            return new PlayerItemPo(item.name(), new SlotKey(playerId, slot, SlotKey.Type.INVENTORY), stackItem.number(), item.color());
        }
        return new PlayerItemPo(item.name(), new SlotKey(playerId, slot, SlotKey.Type.INVENTORY), 1, item.color());
    }

    public static PlayerItemPo toBankItem(long playerId, int slot, Item item) {
        if (item instanceof StackItem stackItem) {
            return new PlayerItemPo(item.name(), new SlotKey(playerId, slot, SlotKey.Type.BANK), stackItem.number(), item.color());
        }
        return new PlayerItemPo(item.name(), new SlotKey(playerId, slot, SlotKey.Type.BANK), 1, item.color());
    }
}
