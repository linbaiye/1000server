package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.y1000.entities.players.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "inventory")
@NoArgsConstructor
@AllArgsConstructor
public class InventoryPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long playerId;

    @JoinColumn
    @JdbcTypeCode(SqlTypes.JSON)
    private List<SlotItem> slots;


    public Set<Long> selectEquipmentIds() {
        return slots == null ? Collections.emptySet() :
                slots.stream().filter(SlotItem::isEquipment)
                        .map(SlotItem::getEquipmentId)
                        .collect(Collectors.toSet());
    }

    public void merge(Inventory inventory) {
        Validate.notNull(inventory);
        slots = new ArrayList<>();
        inventory.foreach((slot, item) -> slots.add(SlotItem.of(slot, item)));
    }

    public static InventoryPo convert(long playerId, Inventory inventory) {
        Validate.notNull(inventory);
        var ret = new InventoryPo(null, playerId, null);
        ret.merge(inventory);
        return ret;
    }
}
