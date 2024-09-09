package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

import java.io.Serializable;

@Data
@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
public class ItemPo {

    private String name;

    @EmbeddedId
    private ItemKey itemKey;

    private long number;

    @Data
    @Builder
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ItemKey implements Serializable {

        private long playerId;

        private int slot;
    }

    public static ItemPo convert(long playerId, int slot, Item item) {
        if (item instanceof StackItem stackItem) {
            return new ItemPo(item.name(), new ItemKey(playerId, slot), ((StackItem) item).number());
        }
        return new ItemPo(item.name(), new ItemKey(playerId, slot), 1);
    }
}
