package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

import java.io.Serializable;

@Data
@Entity
@Table(name = "player_item")
@NoArgsConstructor
@AllArgsConstructor
public class ItemPo {

    private String name;

    @EmbeddedId
    private ItemKey itemKey;

    private long number;

    private int color;

    public enum Type {
        BANK,
        INVENTORY,
    }

    @Data
    @Builder
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ItemKey implements Serializable {

        private long playerId;

        private int slot;

        @Enumerated(EnumType.STRING)
        private Type type;
    }

    public static ItemPo toInventoryItem(long playerId, int slot, Item item) {
        if (item instanceof StackItem stackItem) {
            return new ItemPo(item.name(), new ItemKey(playerId, slot, Type.INVENTORY), stackItem.number(), item.color());
        }
        return new ItemPo(item.name(), new ItemKey(playerId, slot, Type.INVENTORY), 1, item.color());
    }

    public static ItemPo toBankItem(long playerId, int slot, Item item) {
        if (item instanceof StackItem stackItem) {
            return new ItemPo(item.name(), new ItemKey(playerId, slot, Type.BANK), stackItem.number(), item.color());
        }
        return new ItemPo(item.name(), new ItemKey(playerId, slot, Type.BANK), 1, item.color());
    }
}
