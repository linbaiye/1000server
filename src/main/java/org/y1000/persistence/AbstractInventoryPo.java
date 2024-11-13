package org.y1000.persistence;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.y1000.entities.players.inventory.AbstractInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractInventoryPo {

    @JoinColumn
    @JdbcTypeCode(SqlTypes.JSON)
    private List<SlotItem> slots;


    public void merge(AbstractInventory inventory) {
        Validate.notNull(inventory);
        slots = new ArrayList<>();
        inventory.foreach((slot, item) -> slots.add(SlotItem.of(slot, item)));
    }

    public Set<Long> selectEquipmentIds() {
        return slots == null ? Collections.emptySet() :
                slots.stream().filter(SlotItem::isEquipment)
                        .map(SlotItem::getEquipmentId)
                        .collect(Collectors.toSet());
    }
}
